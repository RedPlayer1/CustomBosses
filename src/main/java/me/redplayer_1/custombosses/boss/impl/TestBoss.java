package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.abilities.impl.FireRingAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class TestBoss extends Boss {
    public TestBoss() {
        super(new BossConfig("ZOMBIE", "Test Boss", 20, 15), new FireRingAbility());
    }

    @Override
    public Boss copy() {
        return new TestBoss();
    }

    @Override
    public void onPreSpawn(Location spawnLocation) {
        spawnLocation.getWorld().playEffect(spawnLocation, Effect.ELECTRIC_SPARK, null);
        spawnLocation.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, spawnLocation, 20, 0, 1, 0);
    }

    @Override
    public void onSpawn() {
        // boss is a zombie, so we disable fire damage for it
        ((LivingEntity) getEntity().getEntity()).addPotionEffect(
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1, false, false)
        );
    }

    @Override
    public void onKill(@Nullable LivingEntity killer) {
        Location loc = getEntity().getStoredLocation();
        loc.getWorld().spawnParticle(Particle.HEART, loc, 10);
    }
}
