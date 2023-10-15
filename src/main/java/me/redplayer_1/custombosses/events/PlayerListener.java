package me.redplayer_1.custombosses.events;

import me.redplayer_1.custombosses.boss.Boss;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements Listener {
    private static final HashMap<UUID, TimedPlayerDamager> lastDamagers = new HashMap<>();
    @EventHandler
    public void entityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity damager) {
            lastDamagers.put(player.getUniqueId(), new TimedPlayerDamager(damager, System.currentTimeMillis()));
        }
    }
    @EventHandler
    public void onDeathByNPC(PlayerDeathEvent event) {
        Player killed = event.getPlayer();
        if (killed.getLastDamageCause() == null || !(killed.getLastDamageCause().getEntity() instanceof LivingEntity)) {
            return;
        }
        // get entity that last damaged the player & check its validity
        TimedPlayerDamager lastDamager = lastDamagers.get(killed.getUniqueId());
        if (lastDamager == null) return;
        if ((System.currentTimeMillis() - lastDamager.timeMillis) > 30 * 1000 /* 30 seconds */) {
            lastDamagers.remove(killed.getUniqueId());
            return;
        }
        LivingEntity killer = lastDamager.damager;

        // alter the kill message if it's a boss
        if (killer.hasMetadata("NPC") && killer.hasMetadata(Boss.UUID_METADATA_KEY)) {
            // get the uuid from the boss's metadata
            Boss boss = Boss.getBoss(UUID.fromString(killer.getMetadata(Boss.UUID_METADATA_KEY).get(0).asString()));
            if (boss != null) {
                event.deathMessage(Component.text(killed.getName() + " was killed by a " + boss.getConfig().getName()));
            }
        }
    }

    record TimedPlayerDamager(LivingEntity damager, long timeMillis) {
    }
}

