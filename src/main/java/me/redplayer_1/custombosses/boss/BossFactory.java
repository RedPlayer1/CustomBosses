package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.boss.impl.TestAbilityBoss;
import me.redplayer_1.custombosses.boss.impl.TestBoss;
import org.jetbrains.annotations.Nullable;

public class BossFactory {
    /**
     * Create a new {@link Boss} of that type
     *
     * @param type the type of boss
     * @return a new instance of that boss
     */
    public static Boss create(BossType type) {
        return switch (type) {
            case TEST_BOSS -> new TestBoss();
            case TEST_ABILITY_BOSS -> new TestAbilityBoss();
        };
    }

    /**
     * @return the corresponding {@link BossType} of the boss
     */
    public static @Nullable BossType typeOf(Boss boss) {
        if (boss instanceof TestBoss) {
            return BossType.TEST_BOSS;
        } else if (boss instanceof TestAbilityBoss) {
            return BossType.TEST_ABILITY_BOSS;
        }
        return null;
    }
}
