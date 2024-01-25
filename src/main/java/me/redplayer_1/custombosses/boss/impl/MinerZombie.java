package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinerZombie implements Boss {
    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) { }

    public void onSpawn(BossEntity boss) {
        EntityEquipment equipment = boss.getMob().getEntity().getEquipment();
        assert equipment != null;

        equipment.setHelmet(new ItemStack(Material.SEA_LANTERN));
        equipment.setHelmetDropChance(0);
        equipment.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        equipment.setChestplateDropChance(0);
        equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        equipment.setLeggingsDropChance(0);
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        equipment.setBootsDropChance(0);

        equipment.setItemInMainHand(new ItemStack(Material.NETHERITE_PICKAXE));
        equipment.setItemInMainHandDropChance(0);
    }

    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) { }
}
