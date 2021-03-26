package com.coxclipboard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("coxclipboard")
public interface CoxClipboardConfig extends Config
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
					CoxClipboardPlugin.T_POINTS_PATTERN + ": total points<br>" +
					CoxClipboardPlugin.P_POINTS_PATTERN + ": personal points<br>" +
					CoxClipboardPlugin.KC_PATTERN + ": kill count<br>" +
					CoxClipboardPlugin.SIZE_PATTERN + ": team size"
	)

	default String coxInfoFormat()
	{
		return CoxClipboardPlugin.T_POINTS_PATTERN + ", " +
				CoxClipboardPlugin.P_POINTS_PATTERN + ", " +
				CoxClipboardPlugin.KC_PATTERN + ", " +
				CoxClipboardPlugin.SIZE_PATTERN + ", ";
	}

	@ConfigItem(
			keyName = "tobInfoFormat",
			name = "ToB clipboard format",
			description = "Type in the format of the info to clipboard.<br>" +
					"Available variables:<br>" +
					CoxClipboardPlugin.DEATHS_PATTERN + ": total deaths<br>" +
					CoxClipboardPlugin.KC_PATTERN + ": kill count<br>" +
					CoxClipboardPlugin.SIZE_PATTERN + ": team size<br>" +
					CoxClipboardPlugin.REWARD_PATTERN + ": reward value"
	)
	default String tobInfoFormat()
	{
		return CoxClipboardPlugin.DEATHS_PATTERN + ", " +
				CoxClipboardPlugin.KC_PATTERN + ", " +
				CoxClipboardPlugin.SIZE_PATTERN + ", " +
				CoxClipboardPlugin.REWARD_PATTERN + ", ";
	}
}
