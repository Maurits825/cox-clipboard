package com.raidsclipboard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raidsclipboard")
public interface RaidsClipboardConfig extends Config
{
	@ConfigItem(
			keyName = "clipboardChatMessage",
			name = "Show chat message when copied",
			description = "Adds a chat message with the raids info that is copied to the clipboard.",
			position = 0
	)
	default boolean clipboardChatMessage()
	{
		return false;
	}

	@ConfigItem(
			keyName = "coxInfoFormat",
			name = "CoX clipboard format",
			description = "Type in the format of the info to clipboard.<br>" +
					"Available variables:<br>" +
					RaidsClipboardPlugin.T_POINTS_PATTERN + ": total points<br>" +
					RaidsClipboardPlugin.P_POINTS_PATTERN + ": personal points<br>" +
					RaidsClipboardPlugin.KC_PATTERN + ": kill count<br>" +
					RaidsClipboardPlugin.SIZE_PATTERN + ": team size"
	)

	default String coxInfoFormat()
	{
		return RaidsClipboardPlugin.T_POINTS_PATTERN + ", " +
				RaidsClipboardPlugin.P_POINTS_PATTERN + ", " +
				RaidsClipboardPlugin.KC_PATTERN + ", " +
				RaidsClipboardPlugin.SIZE_PATTERN + ", ";
	}

	@ConfigItem(
			keyName = "tobInfoFormat",
			name = "ToB clipboard format",
			description = "Type in the format of the info to clipboard.<br>" +
					"Available variables:<br>" +
					RaidsClipboardPlugin.DEATHS_PATTERN + ": total deaths<br>" +
					RaidsClipboardPlugin.KC_PATTERN + ": kill count<br>" +
					RaidsClipboardPlugin.SIZE_PATTERN + ": team size<br>" +
					RaidsClipboardPlugin.REWARD_PATTERN + ": reward value"
	)
	default String tobInfoFormat()
	{
		return RaidsClipboardPlugin.DEATHS_PATTERN + ", " +
				RaidsClipboardPlugin.KC_PATTERN + ", " +
				RaidsClipboardPlugin.SIZE_PATTERN + ", " +
				RaidsClipboardPlugin.REWARD_PATTERN + ", ";
	}
}
