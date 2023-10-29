package me.redplayer_1.custombosses.config.providers;

public class BossConfig {
    private final String entityType;
    private final String name;
    private final double health;
    private final double attackRange; // range  that the boss will target/chase a player
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
