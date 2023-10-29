package me.redplayer_1.custombosses.api;

import org.bukkit.NamespacedKey;

public abstract class BossArmor {
    private final double protection;
    private final NamespacedKey key;

    public BossArmor(double protection, NamespacedKey key) {
        this.protection = protection;
        this.key = key;
    }

    /**
     * Fired when the player wearing the armor is damaged by any entity
     *
     * @return the damage the player should take
     */
    public abstract int onHit(/* damager & damaged*/);

    /**
     * Fired when the player wearing the armor is damaged by a boss
     *
     * @return the damage the player should take
     */
    public int onBossHit(/* damager (boss) & damaged*/) {
        return onHit();
    }
}
