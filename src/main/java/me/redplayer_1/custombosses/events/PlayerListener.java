package me.redplayer_1.custombosses.events;

import me.redplayer_1.custombosses.api.PlayerStats;
import me.redplayer_1.custombosses.boss.Trophy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // add player to stat registry
        new PlayerStats(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // save player's stats
        PlayerStats stats = PlayerStats.getRegistry().get(event.getPlayer().getUniqueId());
        if (stats != null) {
            stats.save(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(Trophy.isTrophy(event.getItemInHand()));
    }
}
