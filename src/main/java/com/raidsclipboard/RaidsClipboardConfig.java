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
					"$t_pts: total points<br>" +
					"$p_pts: personal points<br>" +
					"$kc: kill count<br>" +
					"$size: team size"
	)

	default String coxInfoFormat()
	{
		return "";
	}

	@ConfigItem(
			keyName = "tobInfoFormat",
			name = "ToB clipboard format",
			description = "Type in the format of the info to clipboard.<br>" +
					"Available variables:<br>" +
					"$deaths: total deaths<br>" +
					"$kc: kill count<br>" +
					"$size: team size<br>" +
					"$reward: reward value"
	)
	default String tobInfoFormat()
	{
		return "";
	}

	@ConfigItem(
			keyName = "toaInfoFormat",
			name = "ToA clipboard format",
			description = "Type in the format of the info to clipboard.<br>" +
					"Available variables:<br>" +
					"$kc: kill count<br>" +
					"$deaths: total deaths<br>" +
					"$raid_lvl: raid level<br>" +
					"$invo_count: invocation count<br>" +
					"$size: team size<br>"
	)
	default String toaInfoFormat()
	{
		return "";
	}
}
