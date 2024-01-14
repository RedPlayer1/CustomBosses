package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.abilities.impl.*;

// Will have to be reworked when adding user defined Abilities
public enum Abilities {
    FIRE_RING,
    INVINCIBILITY,
    MINION,
    REGEN,
    STASIS,
    STRENGTH,
    THUNDERSTORM;

    // the chance for the ability to fire
    private double chance = 0;

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

    /**
     * @param chance the chance for the ability to fire when chosen
     * @return a new instance of the ability's class
     */
    public BossAbility create() {
        return switch (this) {
            case FIRE_RING -> new FireRingAbility(chance);
            case INVINCIBILITY -> new InvincibilityAbility(chance);
            case MINION -> new MinionAbility(chance);
            case REGEN -> new RegenAbility(chance);
            case STASIS -> new StasisAbility(chance);
            case STRENGTH -> new StrengthAbility(chance);
            case THUNDERSTORM -> new ThunderstormAbility(chance);
        };
    }
}
