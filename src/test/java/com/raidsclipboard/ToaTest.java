package com.raidsclipboard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.raidsclipboard.raids.Toa;
import junit.framework.TestCase;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

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

    }
}
