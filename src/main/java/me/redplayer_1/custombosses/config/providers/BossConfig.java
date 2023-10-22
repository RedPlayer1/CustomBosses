package me.redplayer_1.custombosses.config.providers;

import me.redplayer_1.custombosses.util.LocationUtils;

public class BossConfig {
    private String entityType;
    private String name;
    private double health;
    private double attackRange; // range  that the boss will target/chase a player
    //TODO: abilities & egg

    public BossConfig() {
        entityType = "ZOMBIE";
        name = "Unnamed Boss";
        health = 10;
        attackRange = 15;
    }

    public BossConfig(String entityType, String name, double health, double attackRange) {
        this.entityType = entityType;
        this.name = name;
        this.health = health;
        this.attackRange = attackRange;
    }

    public String getName() {
        return name;
    }
    public String getEntityType() {
        return entityType;
    }
    public double getHealth() {
        return health;
    }
    public double getAttackRange() {
        return attackRange;
    }
}
