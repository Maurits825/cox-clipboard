package com.raidsclipboard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.raidsclipboard.raids.Cox;
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

import static net.runelite.api.ChatMessageType.FRIENDSCHATNOTIFICATION;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CoxTest extends TestCase
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
    private Cox cox;

    @Before
    public void setUp()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        StringSelection selection = new StringSelection("");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @Test
    public void TestOnChatMessage()
    {
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getVarbitValue(Varbits.IN_RAID)).thenReturn(1);

        String format = "kc: " + "$kc" +
                " total points: " + "$t_pts" +
                " personal points: " + "$p_pts" +
                " team size: " + "$size";

        String kc = "73";
        int totalPts = 82000;
        int personalPts = 35000;
        int teamSize = 5;

        when(raidsClipboardConfig.coxInfoFormat()).thenReturn(format);
        when(client.getVarbitValue(Varbits.TOTAL_POINTS)).thenReturn(totalPts);
        when(client.getVarbitValue(Varbits.PERSONAL_POINTS)).thenReturn(personalPts);
        when(client.getVarbitValue(Varbits.RAID_PARTY_SIZE)).thenReturn(teamSize);

        ChatMessage chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "<col=ef20ff>Congratulations - your raid is complete!</col><br>Team size: <col=ff0000>24+ players</col> Duration:</col> <col=ff0000>37:04</col> (new personal best)</col>>", null, 0);
        cox.onChatMessage(chatMessage);

        chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "Your completed Chambers of Xeric count is: <col=ff0000>" + kc + "</col>.", null, 0);
        cox.onChatMessage(chatMessage);

        String clipboardString = getClipboardContent();

        String expected = "kc: " + kc + " total points: " + totalPts +
                " personal points: " + personalPts + " team size: " + teamSize;
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
