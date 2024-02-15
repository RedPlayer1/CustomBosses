package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicHusk implements Boss {
    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) {
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_TNT_PRIMED, 1, 1);
    }

    public void onSpawn(BossEntity boss) {
        EntityEquipment equipment = boss.getMob().getEntity().getEquipment();
        assert equipment != null;

        // give armor that won't drop
        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET), true);
        equipment.setHelmetDropChance(0);
        equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE), true);
        equipment.setChestplateDropChance(0);
        equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS), true);
        equipment.setLeggingsDropChance(0);
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS), true);
        equipment.setBootsDropChance(0);
    }

    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
    }
}
