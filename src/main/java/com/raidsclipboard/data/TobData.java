package com.raidsclipboard.data;

import java.util.regex.Pattern;

public enum TobData implements RaidData
{
    DEATHS(Pattern.compile("\\$deaths")),
    REWARD(Pattern.compile("\\$reward")),
    KILL_COUNT(Pattern.compile("\\$kc")),
    TEAM_SIZE(Pattern.compile("\\$size"));

    private final Pattern pattern;

    TobData(Pattern pattern)
    {
        this.pattern = pattern;
    }

    public Pattern getPattern()
    {
        return pattern;
    }
}
