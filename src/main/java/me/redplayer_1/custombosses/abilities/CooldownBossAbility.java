package me.redplayer_1.custombosses.abilities;

public abstract class CooldownBossAbility implements BossAbility {
    private long lastUse = 0;

    /**
     * Specifies how long the cooldown is
     * @return the cooldown, in ticks (20 tps)
     */
    public abstract long getCooldown();

    /**
     * Checks if the cooldown has expired for this ability. If true is returned,
     * the cooldown is started again.
     * @return whether the ability can be used
     */
    public boolean canUse() {
        if (System.currentTimeMillis() - lastUse > getCooldown()) {
            lastUse = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
}
