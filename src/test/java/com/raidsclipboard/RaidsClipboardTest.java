package com.raidsclipboard;

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
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RaidsClipboardTest extends TestCase
{
    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    private RaidsClipboardConfig raidsClipboardConfig;

    @Mock
    @Bind
    private ChatMessageManager chatMessageManager;

    @Inject
    private RaidsClipboardPlugin raidsClipboardPlugin;

    @Before
    public void setUp()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    public void TestBuildCoxClipboardString()
    {
        Map<RaidsClipboardPlugin.CoxInfo, String> data = new HashMap<>();

        String format = RaidsClipboardPlugin.KC_PATTERN + " " + RaidsClipboardPlugin.P_POINTS_PATTERN;
        String kc = "500";
        String pts = "35000";
        data.put(RaidsClipboardPlugin.CoxInfo.KILL_COUNT, kc);
        data.put(RaidsClipboardPlugin.CoxInfo.PERSONAL_POINTS, pts);

        String clipBoard = raidsClipboardPlugin.buildCoxClipboardString(format, data);
        assertEquals(kc + " " + pts, clipBoard);

        format = "kc: " + RaidsClipboardPlugin.KC_PATTERN +
                " total points: " + RaidsClipboardPlugin.T_POINTS_PATTERN +
                " team size: " + RaidsClipboardPlugin.SIZE_PATTERN;
        kc = "3";
        pts = "76543";
        String teamSize = "4";
        data.put(RaidsClipboardPlugin.CoxInfo.KILL_COUNT, kc);
        data.put(RaidsClipboardPlugin.CoxInfo.TOTAL_POINTS, pts);
        data.put(RaidsClipboardPlugin.CoxInfo.TEAM_SIZE, teamSize);

        clipBoard = raidsClipboardPlugin.buildCoxClipboardString(format, data);
        assertEquals("kc: " + kc + " total points: " + pts + " team size: " + teamSize, clipBoard);
    }

    @Test
    public void TestBuildTobClipboardString()
    {
        Map<RaidsClipboardPlugin.TobInfo, String> data = new HashMap<>();
        String format = "kc: " + RaidsClipboardPlugin.KC_PATTERN + ", " +
                "deaths: " + RaidsClipboardPlugin.DEATHS_PATTERN + ", " +
                "size: " + RaidsClipboardPlugin.SIZE_PATTERN + ", " +
                "reward: " + RaidsClipboardPlugin.REWARD_PATTERN;

        String kc = "50";
        String deaths = "1";
        String size = "3";
        String reward = "540,300";
        data.put(RaidsClipboardPlugin.TobInfo.KILL_COUNT, kc);
        data.put(RaidsClipboardPlugin.TobInfo.DEATHS, deaths);
        data.put(RaidsClipboardPlugin.TobInfo.TEAM_SIZE, size);
        data.put(RaidsClipboardPlugin.TobInfo.REWARD, reward);

        String clipBoard = raidsClipboardPlugin.buildTobClipboardString(format, data);
        assertEquals("kc: " + kc + ", " +
                "deaths: " + deaths + ", " +
                "size: " + size + ", " +
                "reward: " + reward, clipBoard);
    }

    @Test
    public void TestCopyCoxInfoToClipboard()
    {
        Map<RaidsClipboardPlugin.CoxInfo, String> data = new HashMap<>();

        String format = RaidsClipboardPlugin.KC_PATTERN + " " + RaidsClipboardPlugin.P_POINTS_PATTERN;
        String kc = "500";
        String pts = "35000";
        data.put(RaidsClipboardPlugin.CoxInfo.KILL_COUNT, kc);
        data.put(RaidsClipboardPlugin.CoxInfo.PERSONAL_POINTS, pts);

        when(raidsClipboardConfig.coxInfoFormat()).thenReturn(format);

        raidsClipboardPlugin.copyCoxInfoToClipboard(data);

        String clipboardString = getClipboardContent();

        assertEquals(kc + " " + pts, clipboardString);
    }

    @Test
    public void TestCopyTobInfoToClipboard()
    {
        Map<RaidsClipboardPlugin.TobInfo, String> data = new HashMap<>();

        String format = RaidsClipboardPlugin.KC_PATTERN + ", " + RaidsClipboardPlugin.DEATHS_PATTERN;
        String kc = "500";
        String deaths = "5";
        data.put(RaidsClipboardPlugin.TobInfo.KILL_COUNT, kc);
        data.put(RaidsClipboardPlugin.TobInfo.DEATHS, deaths);

        when(raidsClipboardConfig.tobInfoFormat()).thenReturn(format);

        raidsClipboardPlugin.copyTobInfoToClipboard(data);

        String clipboardString = getClipboardContent();

        assertEquals(kc + ", " + deaths, clipboardString);
    }

    @Test
    public void TestCoxOnChatMessage()
    {
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getVar(Varbits.IN_RAID)).thenReturn(1);

        String format = "kc: " + RaidsClipboardPlugin.KC_PATTERN +
                " total points: " + RaidsClipboardPlugin.T_POINTS_PATTERN +
                " personal points: " + RaidsClipboardPlugin.P_POINTS_PATTERN +
                " team size: " + RaidsClipboardPlugin.SIZE_PATTERN;

        String kc = "73";
        int totalPts = 82000;
        int personalPts = 35000;
        int teamSize = 5;

        when(raidsClipboardConfig.coxInfoFormat()).thenReturn(format);
        when(client.getVar(Varbits.TOTAL_POINTS)).thenReturn(totalPts);
        when(client.getVar(Varbits.PERSONAL_POINTS)).thenReturn(personalPts);
        when(client.getVar(Varbits.RAID_PARTY_SIZE)).thenReturn(teamSize);

        ChatMessage chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "<col=ef20ff>Congratulations - your raid is complete!</col><br>Team size: <col=ff0000>24+ players</col> Duration:</col> <col=ff0000>37:04</col> (new personal best)</col>>", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "Your completed Chambers of Xeric count is: <col=ff0000>" + kc + "</col>.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        String clipboardString = getClipboardContent();

        assertEquals("kc: " + kc + " total points: " + totalPts +
                " personal points: " + personalPts + " team size: " + teamSize,
                clipboardString);
    }

    @Test
    public void TestTobOnChatMessage()
    {
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getVar(Varbits.THEATRE_OF_BLOOD)).thenReturn(2);

        Map<Integer, Object> varcmap = new HashMap<>();
        varcmap.put(RaidsClipboardPlugin.TOB_RAIDERS_VARP, "TobPro");
        varcmap.put(RaidsClipboardPlugin.TOB_RAIDERS_VARP + 1, "TobFeeder");

        when(client.getVarcMap()).thenReturn(varcmap);

        String format = "kc: " + RaidsClipboardPlugin.KC_PATTERN +
                " deaths: " + RaidsClipboardPlugin.DEATHS_PATTERN +
                " size: " + RaidsClipboardPlugin.SIZE_PATTERN +
                " reward: " + RaidsClipboardPlugin.REWARD_PATTERN;

        String kc = "73";
        String deaths = "2";
        String size = "2";
        String reward = "450,230";
        String rewardNoComma = reward.replaceAll(",", "");

        when(raidsClipboardConfig.tobInfoFormat()).thenReturn(format);

        ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "<col=ff0000>TobFeeder</col> has died. Death count: <col=ff0000>1</col>.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "You have died. Death count: 2.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "Your completed Theatre of Blood count is: <col=ff0000>" + kc + "</col>.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your loot is worth around <col=ff0000>" + reward + "</col> coins.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        String clipboardString = getClipboardContent();

        assertEquals("kc: " + kc + " deaths: " + deaths +
                        " size: " + size + " reward: " + rewardNoComma,
                clipboardString);

        format = "kc: " + RaidsClipboardPlugin.KC_PATTERN +
                " deaths: " + RaidsClipboardPlugin.DEATHS_PATTERN;

        when(raidsClipboardConfig.tobInfoFormat()).thenReturn(format);

        kc = "100";
        deaths = "1";

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "<col=ff0000>TobFeeder</col> has died. Death count: <col=ff0000>1</col>.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your completed Theatre of Blood count is: <col=ff0000>" + kc + "</col>.", null, 0);
        raidsClipboardPlugin.onChatMessage(chatMessage);

        clipboardString = getClipboardContent();

        assertEquals("kc: " + kc + " deaths: " + deaths, clipboardString);
    }

    @Test
    public void TestShowClipboardTextGameMessage()
    {
        String clipboard = "kc: 100, deaths: 5";
        raidsClipboardPlugin.showClipboardTextGameMessage(clipboard);
        ArgumentCaptor<QueuedMessage> captor = ArgumentCaptor.forClass(QueuedMessage.class);
        verify(chatMessageManager).queue(captor.capture());

        QueuedMessage queuedMessage = captor.getValue();
        assertEquals("Copied to clipboard: " + clipboard, queuedMessage.getRuneLiteFormattedMessage());
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
