package com.coxclipboard;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Cox Clipboard"
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

	private Map<CoxInfo, Pattern> patterns = new HashMap<CoxInfo, Pattern>();
	//private Map<CoxInfo, String> data = new HashMap<CoxInfo, String>();

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

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Cox Clipboard says " + config.greeting(), null);
		}
	}

	@Provides
	CoxClipboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CoxClipboardConfig.class);
	}

	private void initializePatternMap()
	{
		patterns.put(CoxInfo.PERSONAL_POINTS, Pattern.compile("\\$p_pts"));
		patterns.put(CoxInfo.TOTAL_POINTS, Pattern.compile("\\$t_pts"));
		patterns.put(CoxInfo.KILL_COUNT, Pattern.compile("\\$kc"));
		patterns.put(CoxInfo.TEAM_SIZE, Pattern.compile("\\$size"));
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
}
