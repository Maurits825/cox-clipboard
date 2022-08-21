package com.raidsclipboard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.raidsclipboard.raids.Tob;
import junit.framework.TestCase;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatMessageManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.api.ChatMessageType.FRIENDSCHATNOTIFICATION;
import static net.runelite.api.ChatMessageType.GAMEMESSAGE;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TobTest extends TestCase
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
    private Tob tob;

    @Before
    public void setUp()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        StringSelection selection = new StringSelection("");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @Test
    public void TestTobOnChatMessage()
    {
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getVarbitValue(Varbits.THEATRE_OF_BLOOD)).thenReturn(2);

        Map<Integer, Object> varcmap = new HashMap<>();
        varcmap.put(330, "TobPro");
        varcmap.put(330 + 1, "TobFeeder");

        when(client.getVarcMap()).thenReturn(varcmap);

        String format = "kc: " + "$kc" +
                " deaths: " + "$deaths" +
                " size: " + "$size" +
                " reward: " + "$reward";

        String kc = "73";
        String deaths = "3";
        String size = "2";
        String reward = "450,230";
        String rewardNoComma = reward.replaceAll(",", "");

        when(raidsClipboardConfig.tobInfoFormat()).thenReturn(format);

        ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "<col=ff0000>TobFeeder</col> has died. Death count: <col=ff0000>1</col>.", null, 0);
        tob.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "<col=ff0000>TobFeeder</col> has logged out. Death count: <col=ff0000>2</col>.", null, 0);
        tob.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "You have died. Death count: 3.", null, 0);
        tob.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "Your completed Theatre of Blood count is: <col=ff0000>" + kc + "</col>.", null, 0);
        tob.onChatMessage(chatMessage);

        String clipboardString = getClipboardContent();
        assertTrue(clipboardString.isEmpty());

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your loot is worth around <col=ff0000>" + reward + "</col> coins.", null, 0);
        tob.onChatMessage(chatMessage);

        clipboardString = getClipboardContent();
        String expected = "kc: " + kc + " deaths: " + deaths +
                " size: " + size + " reward: " + rewardNoComma;
        assertEquals(expected, clipboardString);

        format = "kc: " + "$kc" +
                " deaths: " + "$deaths";

        when(raidsClipboardConfig.tobInfoFormat()).thenReturn(format);

        kc = "100";
        deaths = "1";

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "<col=ff0000>TobFeeder</col> has died. Death count: <col=ff0000>1</col>.", null, 0);
        tob.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your completed Theatre of Blood count is: <col=ff0000>" + kc + "</col>.", null, 0);
        tob.onChatMessage(chatMessage);

        clipboardString = getClipboardContent();
        expected = "kc: " + kc + " deaths: " + deaths;
        assertEquals(expected, clipboardString);
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
