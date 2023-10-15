package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

public class FireballAbility implements BossAbility {
    @Override
    public void use(Boss boss, Player target) {
        Location bossLoc = boss.getLocation();
        if (bossLoc != null) {
            bossLoc.getWorld().spawnEntity(bossLoc, EntityType.FIREBALL, CreatureSpawnEvent.SpawnReason.CUSTOM,
                    entity -> {
                entity.setVisualFire(false);
                entity.setVelocity(new Vector(2, -2, 2));
                    });
        }

    }
}
