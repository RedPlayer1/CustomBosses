package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.abilities.impl.FireRingAbility;
import me.redplayer_1.custombosses.abilities.impl.ThunderstormAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class TestAbilityBoss extends Boss {

    public TestAbilityBoss() {
        super(new BossConfig("HUSK", "Ability Boss", 10, 7), new ThunderstormAbility(), new FireRingAbility());
    }

    @Override
    public Boss copy() {
        return new TestAbilityBoss();
    }

    @Override
    public void onKill(@Nullable LivingEntity killer) {
        if (killer != null) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 5));
        }
    }

    @Override
    public void onPreSpawn(Location spawnLocation) {
    }

    @Override
    public void onSpawn() {
        // give the boss fire resistance to stop damage from lightning ability
        ((LivingEntity) getEntity().getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1, false, false));
    }
}
