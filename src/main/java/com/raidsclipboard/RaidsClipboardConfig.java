package com.raidsclipboard;

import com.raidsclipboard.data.CoxData;
import com.raidsclipboard.data.TobData;
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
		return CoxData.TOTAL_POINTS + ", " +
				CoxData.PERSONAL_POINTS + ", " +
				CoxData.KILL_COUNT + ", " +
				CoxData.TEAM_SIZE + ", ";
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
		return TobData.DEATHS + ", " +
				TobData.KILL_COUNT + ", " +
				TobData.TEAM_SIZE + ", " +
				TobData.REWARD + ", ";
	}
}
