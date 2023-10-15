package me.redplayer_1.custombosses.api;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public abstract class BossWeapon {
    private final double damage;
    private final NamespacedKey key;

    public BossWeapon(double damage, NamespacedKey key) {
        this.damage = damage;
        this.key = key;
    }

    /**
     * Fired when the player wielding the weapon left clicks with it
     * @param event the triggered event
     * @param entity the entity that was clicked on
     */
    public abstract void onLeftClick(PlayerInteractEvent event, @Nullable Entity entity/* the event (from listener) */);

    /**
     * Fired when the player wielding the weapon right clicks with it
     */
    public abstract void onRightClick(PlayerInteractEvent event/* the event (from listener */);
}
