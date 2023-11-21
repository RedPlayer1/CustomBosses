package me.redplayer_1.custombosses.boss;

import java.util.LinkedList;

public enum BossType {
    TEST_BOSS,
    TEST_ABILITY_BOSS;

    public static final String[] values;

    static {
        LinkedList<String> strVals = new LinkedList<>();
        for (BossType type : values()) {
            strVals.add(type.name());
        }
        values = strVals.toArray(new String[0]);
    }
}
