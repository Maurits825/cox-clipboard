package com.coxclipboard;

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
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Cox Clipboard",
	description = "Copies CoX points info to clipboard"
)
public class CoxClipboardPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CoxClipboardConfig config;

	public enum CoxInfo
	{
		PERSONAL_POINTS, TOTAL_POINTS,
		KILL_COUNT,
		TEAM_SIZE,
	}

	public static final String P_POINTS_PATTERN = "$p_pts";
	public static final String T_POINTS_PATTERN = "$t_pts";
	public static final String KC_PATTERN = "$kc";
	public static final String SIZE_PATTERN = "$size";

	private final Map<CoxInfo, Pattern> patterns = new HashMap<>();
	private final Map<CoxInfo, String> coxRaidData = new HashMap<>();

	private static final String RAID_COMPLETE_MESSAGE = "Congratulations - your raid is complete!";
	private static final Pattern RAIDS_PATTERN = Pattern.compile("Your completed (.+) count is: <col=ff0000>(\\d+)</col>");

	public CoxClipboardPlugin()
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
	CoxClipboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CoxClipboardConfig.class);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (isInRaid() && (event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION || event.getType() == ChatMessageType.GAMEMESSAGE))
		{
			String message = Text.removeTags(event.getMessage());
			if (message.startsWith(RAID_COMPLETE_MESSAGE))
			{
				int totalPoints = client.getVar(Varbits.TOTAL_POINTS);
				int personalPoints = client.getVar(Varbits.PERSONAL_POINTS);
				int teamSize = client.getVar(Varbits.RAID_PARTY_SIZE);

				coxRaidData.put(CoxInfo.TOTAL_POINTS, String.valueOf(totalPoints));
				coxRaidData.put(CoxInfo.PERSONAL_POINTS, String.valueOf(personalPoints));
				coxRaidData.put(CoxInfo.TEAM_SIZE, String.valueOf(teamSize));
				return;
			}

			String messageRaw = event.getMessage();
			Matcher matcher = RAIDS_PATTERN.matcher(messageRaw);
			if (matcher.find())
			{
				int kc = Integer.parseInt(matcher.group(2));
				coxRaidData.put(CoxInfo.KILL_COUNT, String.valueOf(kc));
				copyCoxInfoToClipboard(coxRaidData);
			}
		}
	}

	public void copyCoxInfoToClipboard(Map<CoxInfo, String> values)
	{
		String clipBoardString = buildClipboardString(config.infoFormat(), values);

		StringSelection selection = new StringSelection(clipBoardString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public String buildClipboardString(String format, Map<CoxInfo, String> values)
	{
		String finalStr = format;
		for (CoxInfo coxInfo : CoxInfo.values())
		{
			finalStr = patterns.get(coxInfo).matcher(finalStr).replaceAll(values.get(coxInfo));
		}

		return finalStr;
	}

	private void initializePatternMap()
	{
		patterns.put(CoxInfo.PERSONAL_POINTS, Pattern.compile("\\" + P_POINTS_PATTERN));
		patterns.put(CoxInfo.TOTAL_POINTS, Pattern.compile("\\" + T_POINTS_PATTERN));
		patterns.put(CoxInfo.KILL_COUNT, Pattern.compile("\\" + KC_PATTERN));
		patterns.put(CoxInfo.TEAM_SIZE, Pattern.compile("\\" + SIZE_PATTERN));
	}

	private boolean isInRaid()
	{
		return (client.getGameState() == GameState.LOGGED_IN && client.getVar(Varbits.IN_RAID) == 1);
	}
}
