package me.redplayer_1.custombosses.events;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.entity.MobDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BossListener implements Listener {
    @EventHandler
    public void onMobDeath(MobDeathEvent event) {
        Boss boss = Boss.of(event.getKilled());
        if (boss != null) {
            try {
                boss.kill((LivingEntity) event.getKiller());
            } catch (ClassCastException ex) {
                boss.kill(null);
            }

        }
    }
}
