package me.redplayer_1.custombosses.events;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.entity.Mob;
import me.redplayer_1.custombosses.util.MessageUtils;
import me.redplayer_1.custombosses.util.SyntaxParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DamageListener implements Listener {
    private static final int LAST_DAMAGE_LIFETIME = 30_000; // 30 seconds (in ticks)
    private final SyntaxParser deathMsgParser = new SyntaxParser(new String[]{"{player}", "{boss}"}, new String[0]);

    // damaged uuid to damager
    private static final HashMap<UUID, TimedPlayerDamager> lastDamagers = new HashMap<>();

    static {
        // create a background task to periodically clean lastDamagers
        final long delay = 2;
        Bukkit.getAsyncScheduler().runAtFixedRate(
                CustomBosses.getInstance(),
                task -> lastDamagers.entrySet().removeIf(
                        entry -> System.currentTimeMillis() - entry.getValue().timeMillis > LAST_DAMAGE_LIFETIME),
                delay,
                delay,
                TimeUnit.MINUTES);
    }

    @EventHandler
    public void entityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity damager) {
            // player damaged by boss
            lastDamagers.put(player.getUniqueId(), new TimedPlayerDamager(damager, System.currentTimeMillis()));
        } else if (event.getEntity() instanceof LivingEntity mob && Mob.isMob(mob) && event.getDamager() instanceof LivingEntity damager) {
            // Mob is damaged by entity
            lastDamagers.put(Mob.fromBukkit(mob).getUuid(), new TimedPlayerDamager(damager, System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onDeathByBoss(PlayerDeathEvent event) {
        Player killed = event.getPlayer();
        if (killed.getLastDamageCause() == null || !(killed.getLastDamageCause().getEntity() instanceof LivingEntity)) {
            return;
        }
        // get entity that last damaged the player & check its validity
        LivingEntity killer = getLastDamager(killed.getUniqueId());
        if (killer == null) return;

        // alter the kill message if it's a boss
        Boss boss = Boss.of(killer);
        if (boss != null) {
            // get the uuid from the boss's metadata
            event.deathMessage(MessageUtils.mmsgToComponent(
                    deathMsgParser.parse(CustomBosses.getInstance().getSettings().getConfig().getString("Boss.playerDeathMessage", ""),
                            killed.getName(), boss.getConfig().name())
            ));
        }
    }

    public static @Nullable LivingEntity getLastDamager(UUID damagedUUID) {
        TimedPlayerDamager value = lastDamagers.get(damagedUUID);
        if (value != null) {
            if ((System.currentTimeMillis() - value.timeMillis) > LAST_DAMAGE_LIFETIME) {
                lastDamagers.remove(damagedUUID);
            }
            return value.damager;
        }
        return null;
    }

    /**
     * Removed this uuid from memory to help prevent huge RAM usage
     *
     * @param damagedUUID uuid of damager to remove
     */
    public static void removeLastDamager(UUID damagedUUID) {
        lastDamagers.remove(damagedUUID);
    }

    private record TimedPlayerDamager(LivingEntity damager, long timeMillis) {
    }
}

