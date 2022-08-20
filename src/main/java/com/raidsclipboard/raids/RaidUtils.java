package com.raidsclipboard.raids;

import com.raidsclipboard.data.RaidData;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

public final class RaidUtils
{
    public static void showClipboardTextGameMessage(ChatMessageManager chatMessageManager, String str)
    {
        final String message = new ChatMessageBuilder()
                .append("Copied to clipboard: ")
                .append(str)
                .build();

        chatMessageManager.queue(
                QueuedMessage.builder()
                        .type(ChatMessageType.GAMEMESSAGE)
                        .runeLiteFormattedMessage(message)
                        .build());
    }

    public static String copyRaidInfoToClipboard(String format, Map<RaidData, String> raidData)
    {
        String clipBoardString = buildClipboardString(format, raidData);
        copyStringToClipboard(clipBoardString);

        return clipBoardString;
    }

    private static String buildClipboardString(String format, Map<RaidData, String> raidData)
    {
        String clipboardString = format;

        for (RaidData data : raidData.keySet())
        {
            clipboardString = data.getPattern().matcher(clipboardString).replaceAll(raidData.get(data));
        }

        return clipboardString;
    }

    private static void copyStringToClipboard(String clipboardString)
    {
        StringSelection selection = new StringSelection(clipboardString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
