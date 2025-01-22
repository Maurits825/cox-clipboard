package com.raidsclipboard.raids;

import com.raidsclipboard.data.CoxData;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cox extends Raid
{
    private static final String COMPLETED_MESSAGE = "Congratulations - your raid is complete!";
    private static final Pattern KC_PATTERN = Pattern.compile("Your completed (.+) count is: (\\d+)\\.");
    private static final int RAID_PARTY_SIZE = 5424;

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (isInCox() && (event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION || event.getType() == ChatMessageType.GAMEMESSAGE))
        {
            String message = Text.sanitize(Text.removeTags(event.getMessage()));
            if (message.startsWith(COMPLETED_MESSAGE))
            {
                int totalPoints = client.getVarbitValue(Varbits.TOTAL_POINTS);
                int personalPoints = client.getVarpValue(VarPlayer.RAIDS_PERSONAL_POINTS);
                int teamSize = client.getVarbitValue(RAID_PARTY_SIZE);

                raidData.put(CoxData.TOTAL_POINTS, String.valueOf(totalPoints));
                raidData.put(CoxData.PERSONAL_POINTS, String.valueOf(personalPoints));
                raidData.put(CoxData.TEAM_SIZE, String.valueOf(teamSize));
                return;
            }

            Matcher matcher = KC_PATTERN.matcher(message);
            if (matcher.find())
            {
                raidData.put(CoxData.KILL_COUNT, matcher.group(2));
                handleRaidInfoToClipboard(config.coxInfoFormat());
            }
        }
    }

    private boolean isInCox()
    {
        return (client.getGameState() == GameState.LOGGED_IN && client.getVarbitValue(Varbits.IN_RAID) == 1);
    }
}
