package me.redplayer_1.custombosses.config.providers;

public class BossConfig {
    private String entityType;
    private String name;
    private double health;
    //TODO: abilities & egg

    public BossConfig() {
        entityType = "ZOMBIE";
        name = "Unnamed Boss";
        health = 10;
    }

    public BossConfig(String entityType, String name, double health) {
        this.entityType = entityType;
        this.name = name;
        this.health = health;
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
}
