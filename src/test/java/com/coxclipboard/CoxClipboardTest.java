package com.coxclipboard;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CoxClipboardTest extends TestCase
{
    @Test
    public void TestBuildClipboardString()
    {
        CoxClipboardPlugin coxClipboardPlugin = new CoxClipboardPlugin();
        Map<CoxClipboardPlugin.CoxInfo, String> data = new HashMap<CoxClipboardPlugin.CoxInfo, String>();

        String format = "$kc $p_pts";
        data.put(CoxClipboardPlugin.CoxInfo.KILL_COUNT, "500");
        data.put(CoxClipboardPlugin.CoxInfo.PERSONAL_POINTS, "35000");

        String clipBoard = coxClipboardPlugin.buildClipboardString(format, data);
        assertEquals("500 35000", clipBoard);

        format = "kc: $kc total points: $t_pts team size: $size";
        data.put(CoxClipboardPlugin.CoxInfo.KILL_COUNT, "3");
        data.put(CoxClipboardPlugin.CoxInfo.TOTAL_POINTS, "76543");
        data.put(CoxClipboardPlugin.CoxInfo.TEAM_SIZE, "4");

        clipBoard = coxClipboardPlugin.buildClipboardString(format, data);
        assertEquals("kc: 3 total points: 76543 team size: 4", clipBoard);
    }
}
