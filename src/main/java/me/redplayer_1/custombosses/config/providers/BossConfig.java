package me.redplayer_1.custombosses.config.providers;

import me.redplayer_1.custombosses.boss.BossType;

/**
 * @param attackRange range  that the boss will target/chase a player
 */
public record BossConfig(String entityType, BossType bossType, String name, double health, double attackRange) {
    //TODO: abilities & egg

}
