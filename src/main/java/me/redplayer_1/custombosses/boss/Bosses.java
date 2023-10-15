package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.boss.impl.TestBoss;

public enum Bosses {
    TEST_BOSS(new TestBoss());

    static {
        String[] strings = new String[values().length];
        int index = 0;
        for (Bosses i : values()) {
            strings[index] = i.name();
            index ++;
        }
        names = strings;
    }
    private static final String[] names;
    private final Boss boss;
    Bosses(Boss boss) {
        this.boss = boss;
    }

    public Boss get() {
        return boss;
    }

    public static String[] names() {
        return names;
    }
}
