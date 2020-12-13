package com.coxclipboard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("coxclipboard")
public interface CoxClipboardConfig extends Config
{
	@ConfigItem(
			keyName = "infoFormat",
			name = "Clipboard format",
			description = "Type in the format of the info to clipboard.<br>" +
					"Available variables:<br>" +
					CoxClipboardPlugin.T_POINTS_PATTERN + ": total points<br>" +
					CoxClipboardPlugin.P_POINTS_PATTERN + ": personal points<br>" +
					CoxClipboardPlugin.KC_PATTERN + ": kill count<br>" +
					CoxClipboardPlugin.SIZE_PATTERN + ": team size"
	)
	default String infoFormat()
	{
		return CoxClipboardPlugin.P_POINTS_PATTERN;
	}
}
