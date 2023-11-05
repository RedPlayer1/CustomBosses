package me.redplayer_1.custombosses.api.armor;

import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.NamespacedKey;

public interface BossArmor {
    NamespacedKey key = new NamespacedKey(CustomBosses.getInstance(), "BOSS_ARMOR");

    /**
     * Fired when the player wearing the armor is damaged by any entity
     *
     * @return the damage the player should take
     */
    int onDamage(/* damager (boss?) & damaged*/);
}
