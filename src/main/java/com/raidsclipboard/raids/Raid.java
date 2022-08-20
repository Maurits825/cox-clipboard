package com.raidsclipboard.raids;

import com.raidsclipboard.RaidsClipboardConfig;
import com.raidsclipboard.data.RaidData;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageManager;

import javax.inject.Inject;
import javax.inject.Singleton;
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
        String clipboardString = RaidUtils.copyRaidInfoToClipboard(format, raidData);
        if (config.clipboardChatMessage())
        {
            RaidUtils.showClipboardTextGameMessage(chatMessageManager, clipboardString);
        }
    }
}
