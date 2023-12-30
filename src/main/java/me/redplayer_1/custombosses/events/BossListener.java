package me.redplayer_1.custombosses.events;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.entity.MobDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BossListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (Boss.isBoss(event.getEntity()))
            event.getDrops().clear();
    }
    @EventHandler
    public void onMobDeath(MobDeathEvent event) {
        Boss boss = Boss.of(event.getKilledMob());
        if (boss != null) {
            try {
                boss.kill((LivingEntity) event.getKiller());
            } catch (ClassCastException ex) {
                boss.kill(null);
            }

        }
    }
}
