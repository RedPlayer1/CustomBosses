package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class TestBoss extends Boss {
    public TestBoss(BossAbility... abilities) {
        super(new BossConfig("ZOMBIE", "Test Boss", 20), abilities);
    }

    @Override
    public Boss copy() {
        return new TestBoss();
    }

    @Override
    public void spawn(Location loc) {
        loc.getWorld().playEffect(loc, Effect.ELECTRIC_SPARK, null);
        loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 20, 0, 1, 0);
        super.spawn(loc);
    }

    @Override
    public void onKill(@Nullable LivingEntity killer) {
        Location loc = getEntity().getStoredLocation();
        loc.getWorld().spawnParticle(Particle.HEART, loc, 10);
    }
}
