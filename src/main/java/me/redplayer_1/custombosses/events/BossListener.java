package me.redplayer_1.custombosses.events;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossDeathEvent;
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
            BossDeathEvent bossEvent = new BossDeathEvent(boss, event.getKiller());
            event.setCancelled(!bossEvent.callEvent());
        }
    }

    @EventHandler
    public void onBossDeath(BossDeathEvent event) {
        try {
            event.getKilled().kill((LivingEntity) event.getKiller());
        } catch (ClassCastException ex) {
            event.getKilled().kill(null);
        }
    }
}
