package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.abilities.impl.RegenAbility;
import me.redplayer_1.custombosses.abilities.impl.ThunderstormAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossType;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasicZombie extends Boss {
    private static final ItemStack TROPHY = ItemUtils.createItem(
            Material.ZOMBIE_HEAD,
            "<gradient:dark_green:#4dff00>Basic Zombie Trophy</gradient>",
            List.of("<gray>Obtained from killing a <dark_green>Basic Zombie</dark_green>")
            );

    public BasicZombie() {
        super(new BossConfig(
                "ZOMBIE", BossType.BASIC_ZOMBIE, "Basic Zombie", 50, 15),
                new RegenAbility(0.2),
                new ThunderstormAbility(0.8)
        );
    }

    @Override
    public void onPreSpawn(Location spawnLocation, SpawnBuilder builder) {
        spawnLocation.getWorld().spawnParticle(Particle.CLOUD, spawnLocation, 50);
    }

    @Override
    public void onSpawn() {
        getMob().getEntity().addPotionEffect(
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION,
                        1, false, false)
        );
    }

    @Override
    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
        ItemUtils.giveOrDrop(killer, location, TROPHY);
    }


}
