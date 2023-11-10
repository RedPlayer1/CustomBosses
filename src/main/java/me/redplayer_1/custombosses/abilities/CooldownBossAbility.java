package me.redplayer_1.custombosses.abilities;

public abstract class CooldownBossAbility extends BossAbility {
    private final int cooldown;
    private long lastUse = 0;

    public CooldownBossAbility(String name, boolean singleTarget, double chance, int cooldown) {
        super(name, singleTarget, chance);
        this.cooldown = cooldown;
    }

    /**
     * Specifies how long the cooldown is
     *
     * @return the cooldown, in ticks (20 tps)
     */
    public final int getCooldown() {
        return cooldown;
    }

    /**
     * Checks if the cooldown has expired for this ability. If true is returned,
     * the cooldown is started again.
     *
     * @return whether the ability can be used
     */
    public final boolean canUse() {
        if (System.currentTimeMillis() - lastUse > getCooldown()) {
            lastUse = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
}
