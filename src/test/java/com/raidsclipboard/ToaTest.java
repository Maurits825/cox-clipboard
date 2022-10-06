package com.raidsclipboard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.raidsclipboard.raids.Toa;
import junit.framework.TestCase;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
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

import static net.runelite.api.ChatMessageType.FRIENDSCHATNOTIFICATION;
import static net.runelite.api.ChatMessageType.GAMEMESSAGE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToaTest extends TestCase
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
    private Toa toa;

    @Before
    public void setUp()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        StringSelection selection = new StringSelection("");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @Test
    public void TestToaOnChatMessage()
    {
        int partySize = 2;
        int raidLvl = 155;
        when(client.getVarbitValue(14345)).thenReturn(partySize);
        when(client.getVarbitValue(14380)).thenReturn(raidLvl);

        String format = "kc: " + "$kc" +
                " deaths: " + "$deaths" +
                " raid level: " + "$raid_lvl" +
                " raid type: " + "$raid_type" +
                " size: " + "$size" +
                " reward: " + "$reward";

        String kc = "73";
        String deaths = "1";
        String size = String.valueOf(partySize);
        String reward = "450,230";
        String rewardNoComma = reward.replaceAll(",", "");
        String raidType = "Entry Mode";

        when(raidsClipboardConfig.toaInfoFormat()).thenReturn(format);
        when(client.getWidget(481, 40)).thenReturn(mock(Widget.class));

        ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "You enter the Tombs of Amascut " + raidType + "...", null, 0);
        toa.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "<col=ff0000>Feeder</col> has died. Total deaths: <col=ff0000>1</col>.", null, 0);
        toa.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "Your completed Tombs of Amascut: " + raidType + " count is: <col=ff0000>" + kc + "</col>.", null, 0);
        toa.onChatMessage(chatMessage);

        String clipboardString = getClipboardContent();
        assertTrue(clipboardString.isEmpty());

        chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your loot is worth around <col=ff0000>" + reward + "</col> coins.", null, 0);
        toa.onChatMessage(chatMessage);

        clipboardString = getClipboardContent();
        String expected = "kc: " + kc +
                " deaths: " + deaths +
                " raid level: " + raidLvl +
                " raid type: " + raidType +
                " size: " + size +
                " reward: " + rewardNoComma;

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
