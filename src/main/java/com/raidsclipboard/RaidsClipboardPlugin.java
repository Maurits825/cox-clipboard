package com.raidsclipboard;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.raidsclipboard.raids.Cox;
import com.raidsclipboard.raids.Tob;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Raids Clipboard",
	description = "Copies raids info such as kc, points & deaths to clipboard"
)
public class RaidsClipboardPlugin extends Plugin
{
	@Inject
	private RaidsClipboardConfig config;

	@Inject
	private EventBus eventBus;

	@Inject
	private Cox cox;

	@Inject
	private Tob tob;

	@Override
	protected void startUp() throws Exception
	{
		eventBus.register(cox);
		eventBus.register(tob);
	}

	@Override
	protected void shutDown() throws Exception
	{
		eventBus.unregister(cox);
		eventBus.unregister(tob);
	}

	@Provides
	RaidsClipboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RaidsClipboardConfig.class);
	}
}
