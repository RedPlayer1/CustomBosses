package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.abilities.impl.*;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossType;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Bob extends Boss {
    public Bob() {
        super(
                new BossConfig("ZOMBIE", BossType.BOB, "Bob", 1000, 30),
                1,
                new StasisAbility(),
                new StrengthAbility(),
                new FireRingAbility(),
                new MinionAbility(),
                new ThunderstormAbility()
        );
    }

    @Override
    public void onPreSpawn(Location spawnLocation, SpawnBuilder builder) {

    }

    @Override
    public void onSpawn() {
        ItemUtils.applyEquipment(
                getMob().getEntity(),
                0,
                new ItemStack(Material.NETHERITE_HELMET),
                ItemUtils.createItem(Material.NETHERITE_CHESTPLATE, null, null, true, true, new ItemUtils.Enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)),
                new ItemStack(Material.NETHERITE_LEGGINGS),
                new ItemStack(Material.NETHERITE_BOOTS),
                new ItemStack(Material.NETHERITE_AXE),
                null);
    }

    @Override
    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
    }
}
