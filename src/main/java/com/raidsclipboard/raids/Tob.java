package com.raidsclipboard.raids;

import com.raidsclipboard.data.TobData;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tob extends Raid
{
    private static final int TOB_RAIDERS_VARP = 330;
    private static final int TOB_MAX_SIZE = 5;

    private static final Pattern KC_PATTERN = Pattern.compile("Your completed (.+) count is: (\\d+)\\.");
    private static final Pattern REWARD_PATTERN = Pattern.compile("Your loot is worth around (.*) coins\\.");

    private static final Pattern DEATH_SELF = Pattern.compile("You have died. Death count: \\d+\\.");
    private static final Pattern DEATH_OTHER = Pattern.compile(".* has (?:died|logged out). Death count: \\d+\\.");

    private int tobCurrentDeaths = 0;
    private int currentTobState = 0;

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (isInTob() && (event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION || event.getType() == ChatMessageType.GAMEMESSAGE))
        {
            String message = Text.sanitize(Text.removeTags(event.getMessage()));

            Matcher self = DEATH_SELF.matcher(message);
            Matcher other = DEATH_OTHER.matcher(message);
            if (self.matches() || other.matches())
            {
                tobCurrentDeaths++;
                return;
            }

            Matcher matcherKc = KC_PATTERN.matcher(message);
            if (matcherKc.find())
            {
                raidData.put(TobData.KILL_COUNT, matcherKc.group(2));
                raidData.put(TobData.DEATHS, String.valueOf(tobCurrentDeaths));
                raidData.put(TobData.TEAM_SIZE, getTobTeamSize());

                if (!TobData.REWARD.getPattern().matcher(config.tobInfoFormat()).find())
                {
                    handleRaidInfoToClipboard(config.tobInfoFormat());
                }

                tobCurrentDeaths = 0;
                return;
            }

            Matcher matcherRewards = REWARD_PATTERN.matcher(message);
            if (matcherRewards.find() && TobData.REWARD.getPattern().matcher(config.tobInfoFormat()).find())
            {
                raidData.put(TobData.REWARD, matcherRewards.group(1).replaceAll(",", ""));
                handleRaidInfoToClipboard(config.tobInfoFormat());
                tobCurrentDeaths = 0;
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        int nextState = client.getVarbitValue(Varbits.THEATRE_OF_BLOOD);
        if (currentTobState != nextState)
        {
            if (nextState == 2)
            {
                tobCurrentDeaths = 0;
            }
            currentTobState = nextState;
        }
    }

    private boolean isInTob()
    {
        return (client.getGameState() == GameState.LOGGED_IN &&
                ((client.getVarbitValue(Varbits.THEATRE_OF_BLOOD) == 2) || (client.getVarbitValue(Varbits.THEATRE_OF_BLOOD) == 3)));
    }

    private String getTobTeamSize()
    {
        int teamSize = 0;
        Map<Integer, Object> varcmap = client.getVarcMap();
        for (int i = 0; i < TOB_MAX_SIZE; i++) {
            Integer playervarp = TOB_RAIDERS_VARP + i;
            if (varcmap.containsKey(playervarp)) {
                String tName = Text.sanitize(varcmap.get(playervarp).toString());
                if (tName != null && !tName.equals("")) {
                    teamSize++;
                }
            }
        }

        return String.valueOf(teamSize);
    }
}
