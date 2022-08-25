package com.raidsclipboard.raids;

import com.raidsclipboard.data.ToaData;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Toa extends Raid
{
    @RequiredArgsConstructor
    enum RaidType
    {
        ENTRY("Entry Mode"),
        NORMAL("Normal Mode"),
        EXPERT("Expert Mode");

        private final String name;
    }

    private static final int RAID_LEVEL_VAR = 14380;
    private static final int PARTY_SIZE_VAR = 14345;

    private static final Pattern KC_PATTERN = Pattern.compile("Your completed (.+) count is: (\\d+)\\.");
    private static final Pattern ENTRY_PATTERN = Pattern.compile("You enter the Tombs of Amascut");
    private static final Pattern REWARD_PATTERN = Pattern.compile("Your loot is worth around (.*) coins\\.");

    private static final Pattern DEATH_SELF = Pattern.compile("You have died. Death count: \\d+\\.");
    private static final Pattern DEATH_OTHER = Pattern.compile(".* has (?:died|logged out). Death count: \\d+\\.");

    private int currentDeaths = 0;

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if ((event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION || event.getType() == ChatMessageType.GAMEMESSAGE))
        {
            String message = Text.sanitize(Text.removeTags(event.getMessage()));

            Matcher entry = ENTRY_PATTERN.matcher(message);
            if (entry.find())
            {
                String raidType = RaidType.NORMAL.name;
                if (message.contains(RaidType.ENTRY.name))
                {
                    raidType = RaidType.ENTRY.name;
                }
                else if (message.contains(RaidType.EXPERT.name))
                {
                    raidType = RaidType.EXPERT.name;
                }

                raidData.put(ToaData.RAID_TYPE, raidType);
                currentDeaths = 0;
                return;
            }

            Matcher self = DEATH_SELF.matcher(message);
            Matcher other = DEATH_OTHER.matcher(message);
            if (self.matches() || other.matches())
            {
                currentDeaths++;
                return;
            }

            Matcher matcherKc = KC_PATTERN.matcher(message);
            if (matcherKc.find())
            {
                raidData.put(ToaData.KILL_COUNT, matcherKc.group(2));
                raidData.put(ToaData.DEATHS, String.valueOf(currentDeaths));

                int teamSize = client.getVarbitValue(PARTY_SIZE_VAR);
                int raidLvl = client.getVarbitValue(RAID_LEVEL_VAR);

                raidData.put(ToaData.TEAM_SIZE, String.valueOf(teamSize));
                raidData.put(ToaData.RAID_LEVEL, String.valueOf(raidLvl));

                if (!ToaData.REWARD.getPattern().matcher(config.toaInfoFormat()).find())
                {
                    handleRaidInfoToClipboard(config.toaInfoFormat());
                }
                return;
            }

            Matcher matcherRewards = REWARD_PATTERN.matcher(message);
            if (matcherRewards.find())
            {
                raidData.put(ToaData.REWARD, matcherRewards.group(1).replaceAll(",", ""));
                handleRaidInfoToClipboard(config.toaInfoFormat());
            }
        }
    }
}
