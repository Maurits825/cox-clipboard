package com.raidsclipboard.data;

import java.util.regex.Pattern;

public enum ToaData implements RaidData
{
    KILL_COUNT(Pattern.compile("\\$kc")),
    DEATHS(Pattern.compile("\\$deaths")),
    RAID_LEVEL(Pattern.compile("\\$raid_lvl")),
    RAID_TYPE(Pattern.compile("\\$raid_type")),
    REWARD(Pattern.compile("\\$reward")),
    TEAM_SIZE(Pattern.compile("\\$size"));

    private final Pattern pattern;

    ToaData(Pattern pattern)
    {
        this.pattern = pattern;
    }

    public Pattern getPattern()
    {
        return pattern;
    }
}
