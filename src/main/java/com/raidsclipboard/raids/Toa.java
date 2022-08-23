package com.raidsclipboard.raids;

import java.util.regex.Pattern;

public class Toa extends Raid{

    private static final Pattern KC_PATTERN = Pattern.compile("Your completed (.+) count is: (\\d+)\\.");
    private static final Pattern REWARD_PATTERN = Pattern.compile("Your loot is worth around (.*) coins\\.");

    private static final Pattern DEATH_SELF = Pattern.compile("You have died. Death count: \\d+\\.");
    private static final Pattern DEATH_OTHER = Pattern.compile(".* has (?:died|logged out). Death count: \\d+\\.");

}
