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

	public static final String P_POINTS_PATTERN = "$p_pts";
	public static final String T_POINTS_PATTERN = "$t_pts";
	public static final String KC_PATTERN = "$kc";
	public static final String SIZE_PATTERN = "$size";

	private Map<CoxInfo, Pattern> patterns = new HashMap<CoxInfo, Pattern>();

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
	}

	@Provides
	CoxClipboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CoxClipboardConfig.class);
	}

	private void initializePatternMap()
	{
		patterns.put(CoxInfo.PERSONAL_POINTS, Pattern.compile("\\" + P_POINTS_PATTERN));
		patterns.put(CoxInfo.TOTAL_POINTS, Pattern.compile("\\" + T_POINTS_PATTERN));
		patterns.put(CoxInfo.KILL_COUNT, Pattern.compile("\\" + KC_PATTERN));
		patterns.put(CoxInfo.TEAM_SIZE, Pattern.compile("\\" + SIZE_PATTERN));
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
