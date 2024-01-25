package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Bob implements Boss {
    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) { }

    public void onSpawn(BossEntity boss) {
        ItemUtils.applyEquipment(
                boss.getMob().getEntity(),
                0,
                new ItemStack(Material.NETHERITE_HELMET),
                ItemUtils.createItem(Material.NETHERITE_CHESTPLATE, null, null, true, true, new ItemUtils.Enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)),
                new ItemStack(Material.NETHERITE_LEGGINGS),
                new ItemStack(Material.NETHERITE_BOOTS),
                new ItemStack(Material.NETHERITE_AXE),
                null);
    }

    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) { }
}
