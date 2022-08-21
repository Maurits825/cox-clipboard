package com.raidsclipboard.raids;

import com.raidsclipboard.RaidsClipboardConfig;
import com.raidsclipboard.data.RaidData;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public abstract class Raid
{
    @Inject
    protected Client client;

    @Inject
    protected ChatMessageManager chatMessageManager;

    protected final RaidsClipboardConfig config;

    protected final Map<RaidData, String> raidData = new HashMap<>();

    @Inject
    protected Raid(RaidsClipboardConfig config)
    {
        this.config = config;
    }

    protected void handleRaidInfoToClipboard(String format)
    {
        String clipboardString = copyRaidInfoToClipboard(format, raidData);
        if (config.clipboardChatMessage())
        {
            showClipboardTextGameMessage(clipboardString);
        }
    }

    private void showClipboardTextGameMessage(String str)
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

    private String copyRaidInfoToClipboard(String format, Map<RaidData, String> raidData)
    {
        String clipBoardString = buildClipboardString(format, raidData);
        copyStringToClipboard(clipBoardString);

        return clipBoardString;
    }

    private String buildClipboardString(String format, Map<RaidData, String> raidData)
    {
        String clipboardString = format;

        for (RaidData data : raidData.keySet())
        {
            clipboardString = data.getPattern().matcher(clipboardString).replaceAll(raidData.get(data));
        }

        return clipboardString;
    }

    private void copyStringToClipboard(String clipboardString)
    {
        StringSelection selection = new StringSelection(clipboardString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
