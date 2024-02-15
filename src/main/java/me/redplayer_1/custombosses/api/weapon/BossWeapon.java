package me.redplayer_1.custombosses.api.weapon;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public interface BossWeapon {
    NamespacedKey key = new NamespacedKey(CustomBosses.getInstance(), "BOSS_WEAPON");

    /**
     * Fired when the player wielding the weapon left clicks with it
     *
     * @param event      the triggered event
     * @param bossEntity the boss that was clicked on (nullable)
     */
    void onLeftClick(PlayerInteractEvent event, @Nullable BossEntity bossEntity/* the event (from listener) */);

    /**
     * Fired when the player wielding the weapon right clicks with it
     */
    void onRightClick(PlayerInteractEvent event/* the event (from listener */);
}
