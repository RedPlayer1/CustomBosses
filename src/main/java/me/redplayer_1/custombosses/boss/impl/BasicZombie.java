package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicZombie implements Boss {
    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) {
        spawnLocation.getWorld().spawnParticle(Particle.CLOUD, spawnLocation, 50);
    }

    public void onSpawn(BossEntity boss) {
        boss.getMob().getEntity().addPotionEffect(
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION,
                        1, false, false)
        );
    }

    @Override
    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
    }
}
