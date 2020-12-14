package com.coxclipboard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import static net.runelite.api.ChatMessageType.FRIENDSCHATNOTIFICATION;
import static net.runelite.api.ChatMessageType.GAMEMESSAGE;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CoxClipboardTest extends TestCase
{
    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    private CoxClipboardConfig coxClipboardConfig;

    @Inject
    private CoxClipboardPlugin coxClipboardPlugin;

    @Before
    public void setUp()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    public void TestBuildClipboardString()
    {
        Map<CoxClipboardPlugin.CoxInfo, String> data = new HashMap<>();

        String format = CoxClipboardPlugin.KC_PATTERN + " " + CoxClipboardPlugin.P_POINTS_PATTERN;
        String kc = "500";
        String pts = "35000";
        data.put(CoxClipboardPlugin.CoxInfo.KILL_COUNT, kc);
        data.put(CoxClipboardPlugin.CoxInfo.PERSONAL_POINTS, pts);

        String clipBoard = coxClipboardPlugin.buildClipboardString(format, data);
        assertEquals(kc + " " + pts, clipBoard);

        format = "kc: " + CoxClipboardPlugin.KC_PATTERN +
                " total points: " + CoxClipboardPlugin.T_POINTS_PATTERN +
                " team size: " + CoxClipboardPlugin.SIZE_PATTERN;
        kc = "3";
        pts = "76543";
        String teamSize = "4";
        data.put(CoxClipboardPlugin.CoxInfo.KILL_COUNT, kc);
        data.put(CoxClipboardPlugin.CoxInfo.TOTAL_POINTS, pts);
        data.put(CoxClipboardPlugin.CoxInfo.TEAM_SIZE, teamSize);

        clipBoard = coxClipboardPlugin.buildClipboardString(format, data);
        assertEquals("kc: " + kc + " total points: " + pts + " team size: " + teamSize, clipBoard);
    }

    @Test
    public void TestCopyCoxInfoToClipboard() {
        Map<CoxClipboardPlugin.CoxInfo, String> data = new HashMap<>();

        String format = CoxClipboardPlugin.KC_PATTERN + " " + CoxClipboardPlugin.P_POINTS_PATTERN;
        String kc = "500";
        String pts = "35000";
        data.put(CoxClipboardPlugin.CoxInfo.KILL_COUNT, kc);
        data.put(CoxClipboardPlugin.CoxInfo.PERSONAL_POINTS, pts);

        when(coxClipboardConfig.infoFormat()).thenReturn(format);

        coxClipboardPlugin.copyCoxInfoToClipboard(data);

        String clipboardString = getClipboardContent();

        assertEquals(kc + " " + pts, clipboardString);
    }

    @Test
    public void TestOnChatMessage()
    {
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getVar(Varbits.IN_RAID)).thenReturn(1);

        String format = "kc: " + CoxClipboardPlugin.KC_PATTERN +
                " total points: " + CoxClipboardPlugin.T_POINTS_PATTERN +
                " personal points: " + CoxClipboardPlugin.P_POINTS_PATTERN +
                " team size: " + CoxClipboardPlugin.SIZE_PATTERN;

        String kc = "73";
        int totalPts = 82000;
        int personalPts = 35000;
        int teamSize = 5;

        when(coxClipboardConfig.infoFormat()).thenReturn(format);
        when(client.getVar(Varbits.TOTAL_POINTS)).thenReturn(totalPts);
        when(client.getVar(Varbits.PERSONAL_POINTS)).thenReturn(personalPts);
        when(client.getVar(Varbits.RAID_PARTY_SIZE)).thenReturn(teamSize);

        ChatMessage chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "<col=ef20ff>Congratulations - your raid is complete!</col><br>Team size: <col=ff0000>24+ players</col> Duration:</col> <col=ff0000>37:04</col> (new personal best)</col>>", null, 0);
        coxClipboardPlugin.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your completed Chambers of Xeric count is: <col=ff0000>" + kc + "</col>.", null, 0);
        coxClipboardPlugin.onChatMessage(chatMessage);

        String clipboardString = getClipboardContent();

        assertEquals("kc: " + kc + " total points: " + totalPts +
                " personal points: " + personalPts + " team size: " + teamSize,
                clipboardString);
    }

    private String getClipboardContent()
    {
        String clipboardString;
        try {
            clipboardString = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            clipboardString = null;
        }

        return clipboardString;
    }
}
