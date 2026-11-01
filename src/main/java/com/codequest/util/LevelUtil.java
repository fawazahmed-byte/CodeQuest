package com.codequest.util;

/**
 * Module 8 / 10 - Dashboard and XP system
 *
 * Level rule:  level = (totalXp / xpPerLevel) + 1
 * xpPerLevel comes from the web.xml context-param "xpPerLevel" (default 100).
 * Example with 100 XP per level: 0-99 XP = Level 1, 100-199 XP = Level 2, ...
 */
public class LevelUtil {

    public static final int DEFAULT_XP_PER_LEVEL = 100;

    /**
     * Converts the context-param text into a number.
     * Falls back to 100 if it is missing or invalid.
     */
    public static int parseXpPerLevel(String value) {
        try {
            int xp = Integer.parseInt(value.trim());
            return xp > 0 ? xp : DEFAULT_XP_PER_LEVEL;
        } catch (Exception e) {
            return DEFAULT_XP_PER_LEVEL;
        }
    }

    /** Level for a given amount of XP. */
    public static int levelFor(int xp, int xpPerLevel) {
        return (xp / xpPerLevel) + 1;
    }

    /** Total XP needed to reach the level after the given level. */
    public static int nextLevelXp(int level, int xpPerLevel) {
        return level * xpPerLevel;
    }

    /** How far (0-99 %) the player is inside the current level. */
    public static int progressPercent(int xp, int xpPerLevel) {
        return (xp % xpPerLevel) * 100 / xpPerLevel;
    }
}
