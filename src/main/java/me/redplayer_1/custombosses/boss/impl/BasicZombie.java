package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.boss.Trophy;
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

public class BasicZombie implements Boss {
    private static final ItemStack TROPHY = new Trophy(
            Material.ZOMBIE_HEAD,
            "<gradient:dark_green:#4dff00>Basic Zombie Trophy</gradient>",
            List.of("<gray>Obtained from killing a <dark_green>Basic Zombie</dark_green>")
    );

    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) {
        spawnLocation.getWorld().spawnParticle(Particle.CLOUD, spawnLocation, 50);
    }

    public void onSpawn(BossEntity boss) {
        boss.getMob().getEntity().addPotionEffect(
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION,
                        1, false, false)
        );
    }

    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
        ItemUtils.giveOrDrop(killer, location, TROPHY);
    }


}
