package com.raidsclipboard;

import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.client.chat.ChatMessageManager;

@Slf4j
@PluginDescriptor(
	name = "Raids Clipboard",
	description = "Copies raids info such as kc, points & deaths to clipboard"
)
public class RaidsClipboardPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private RaidsClipboardConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	public enum CoxInfo
	{
		PERSONAL_POINTS, TOTAL_POINTS,
		KILL_COUNT,
		TEAM_SIZE,
	}

	public enum TobInfo
	{
		DEATHS,
		KILL_COUNT,
		TEAM_SIZE,
		REWARD,
	}

	public static final int TOB_RAIDERS_VARP = 330;
	public static final int TOB_MAX_SIZE = 5;

	public static final String P_POINTS_PATTERN = "$p_pts";
	public static final String T_POINTS_PATTERN = "$t_pts";
	public static final String KC_PATTERN = "$kc";
	public static final String SIZE_PATTERN = "$size";
	public static final String DEATHS_PATTERN = "$deaths";
	public static final String REWARD_PATTERN = "$reward";

	private final Map<CoxInfo, Pattern> coxPatterns = new HashMap<>();
	private final Map<CoxInfo, String> coxRaidData = new HashMap<>();

	private final Map<TobInfo, Pattern> tobPatterns = new HashMap<>();
	private final Map<TobInfo, String> tobRaidData = new HashMap<>();

	private static final String COX_COMPLETE_MESSAGE = "Congratulations - your raid is complete!";
	private static final Pattern RAIDS_KC_PATTERN = Pattern.compile("Your completed (.+) count is: <col=ff0000>(\\d+)</col>\\.");

	private static final Pattern RAIDS_REWARD_PATTERN = Pattern.compile("Your loot is worth around (.*) coins\\.");

	public static final Pattern DEATH_SELF = Pattern.compile("You have died. Death count: \\d+\\.");
	public static final Pattern DEATH_OTHER = Pattern.compile(".* has died. Death count: \\d+\\.");

	private int tobVarState = 0;
	private int tobCurrentDeaths = 0;

	public RaidsClipboardPlugin()
	{
		initializePatternMap();
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Cox Clipboard started!");
		initializePatternMap();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Cox Clipboard stopped!");
	}

	@Provides
	RaidsClipboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RaidsClipboardConfig.class);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (isInCox() && (event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION || event.getType() == ChatMessageType.GAMEMESSAGE))
		{
			String messageRaw = event.getMessage();
			String message = Text.removeTags(messageRaw);
			if (message.startsWith(COX_COMPLETE_MESSAGE))
			{
				int totalPoints = client.getVar(Varbits.TOTAL_POINTS);
				int personalPoints = client.getVar(Varbits.PERSONAL_POINTS);
				int teamSize = client.getVar(Varbits.RAID_PARTY_SIZE);

				coxRaidData.put(CoxInfo.TOTAL_POINTS, String.valueOf(totalPoints));
				coxRaidData.put(CoxInfo.PERSONAL_POINTS, String.valueOf(personalPoints));
				coxRaidData.put(CoxInfo.TEAM_SIZE, String.valueOf(teamSize));
				return;
			}

			Matcher matcher = RAIDS_KC_PATTERN.matcher(messageRaw);
			if (matcher.find())
			{
				coxRaidData.put(CoxInfo.KILL_COUNT, matcher.group(2));
				copyCoxInfoToClipboard(coxRaidData);
			}
		}
		else if (isInTob() && (event.getType() == ChatMessageType.GAMEMESSAGE))
		{
			String messageRaw = event.getMessage();
			String msg = Text.sanitize(Text.removeTags(messageRaw));

			Matcher self = DEATH_SELF.matcher(msg);
			Matcher other = DEATH_OTHER.matcher(msg);
			if (self.matches() || other.matches())
			{
				tobCurrentDeaths++;
				return;
			}

			Matcher matcherKc = RAIDS_KC_PATTERN.matcher(messageRaw);
			if (matcherKc.find())
			{
				tobRaidData.put(TobInfo.KILL_COUNT, matcherKc.group(2));
				tobRaidData.put(TobInfo.DEATHS, String.valueOf(tobCurrentDeaths));
				tobRaidData.put(TobInfo.TEAM_SIZE, getTobTeamSize());

				if (!config.tobInfoFormat().contains(REWARD_PATTERN))
				{
					copyTobInfoToClipboard(tobRaidData);
				}

				tobCurrentDeaths = 0;
				return;
			}

			Matcher matcherRewards = RAIDS_REWARD_PATTERN.matcher(msg);
			if (matcherRewards.find())
			{
				tobRaidData.put(TobInfo.REWARD, matcherRewards.group(1));
				copyTobInfoToClipboard(tobRaidData);
			}
		}
	}

	public void copyCoxInfoToClipboard(Map<CoxInfo, String> values)
	{
		String clipBoardString = buildCoxClipboardString(config.coxInfoFormat(), values);
		copyStringToClipboard(clipBoardString);
	}

	public String buildCoxClipboardString(String format, Map<CoxInfo, String> values)
	{
		String finalStr = format;
		for (CoxInfo coxInfo : CoxInfo.values())
		{
			finalStr = coxPatterns.get(coxInfo).matcher(finalStr).replaceAll(values.get(coxInfo));
		}

		return finalStr;
	}

	public void copyTobInfoToClipboard(Map<TobInfo, String> values)
	{
		String clipBoardString = buildTobClipboardString(config.tobInfoFormat(), values);
		copyStringToClipboard(clipBoardString);
	}

	public String buildTobClipboardString(String format, Map<TobInfo, String> values)
	{
		String finalStr = format;
		for (TobInfo tobInfo : TobInfo.values())
		{
			finalStr = tobPatterns.get(tobInfo).matcher(finalStr).replaceAll(values.get(tobInfo));
		}

		return finalStr;
	}

	public void copyStringToClipboard(String clipboardString)
	{
		StringSelection selection = new StringSelection(clipboardString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);

		if (config.clipboardChatMessage())
		{
			showClipboardTextGameMessage(clipboardString);
		}
	}

	public void showClipboardTextGameMessage(String str)
	{
		final String message = new ChatMessageBuilder()
				.append("Copied to clipboard: ")
				.append(str)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.GAMEMESSAGE)
						.runeLiteFormattedMessage(message)
						.build());
	}

	private void initializePatternMap()
	{
		coxPatterns.put(CoxInfo.PERSONAL_POINTS, Pattern.compile("\\" + P_POINTS_PATTERN));
		coxPatterns.put(CoxInfo.TOTAL_POINTS, Pattern.compile("\\" + T_POINTS_PATTERN));
		coxPatterns.put(CoxInfo.KILL_COUNT, Pattern.compile("\\" + KC_PATTERN));
		coxPatterns.put(CoxInfo.TEAM_SIZE, Pattern.compile("\\" + SIZE_PATTERN));

		tobPatterns.put(TobInfo.DEATHS, Pattern.compile("\\" + DEATHS_PATTERN));
		tobPatterns.put(TobInfo.KILL_COUNT, Pattern.compile("\\" + KC_PATTERN));
		tobPatterns.put(TobInfo.TEAM_SIZE, Pattern.compile("\\" + SIZE_PATTERN));
		tobPatterns.put(TobInfo.REWARD, Pattern.compile("\\" + REWARD_PATTERN));
	}

	private boolean isInCox()
	{
		return (client.getGameState() == GameState.LOGGED_IN && client.getVar(Varbits.IN_RAID) == 1);
	}

	private boolean isInTob()
	{
		return (client.getGameState() == GameState.LOGGED_IN &&
				((client.getVar(Varbits.THEATRE_OF_BLOOD) == 2) || (client.getVar(Varbits.THEATRE_OF_BLOOD) == 3)));
	}

	private String getTobTeamSize()
	{
		int teamSize = 0;
		Map<Integer, Object> varcmap = client.getVarcMap();
		for (int i = 0; i < TOB_MAX_SIZE; i++) {
			Integer playervarp = TOB_RAIDERS_VARP + i;
			if (varcmap.containsKey(playervarp)) {
				String tName = Text.sanitize(varcmap.get(playervarp).toString());
				if (tName != null && !tName.equals("")) {
					teamSize++;
				}
			}
		}

		return String.valueOf(teamSize);
	}
}
