package com.raidsclipboard.data;

import java.util.regex.Pattern;

public enum CoxData implements RaidData
{
    PERSONAL_POINTS(Pattern.compile("\\$p_pts")),
    TOTAL_POINTS(Pattern.compile("\\$t_pts")),
    KILL_COUNT(Pattern.compile("\\$kc")),
    TEAM_SIZE(Pattern.compile("\\$size"));

    private final Pattern pattern;

    CoxData(Pattern pattern)
    {
        this.pattern = pattern;
    }

    public Pattern getPattern()
    {
        return pattern;
    }
}
