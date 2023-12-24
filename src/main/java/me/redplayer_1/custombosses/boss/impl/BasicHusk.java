package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.abilities.impl.FireRingAbility;
import me.redplayer_1.custombosses.abilities.impl.RegenAbility;
import me.redplayer_1.custombosses.abilities.impl.StrengthAbility;
import me.redplayer_1.custombosses.abilities.impl.ThunderstormAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossType;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasicHusk extends Boss {
    private static final ItemStack TROPHY = ItemUtils.createItem(
            Material.DEAD_BUSH,
            "<gradient:#ff5c26:yellow>Basic Husk Trophy</gradient>",
            List.of("<gray>Obtained from killing a <color:#ff5c26>Basic Husk</color>")
    );

    public BasicHusk() {
        super(
                new BossConfig("HUSK", BossType.BASIC_HUSK, "Basic Husk", 100, 20),
                new FireRingAbility(0.4),
                new StrengthAbility(0.9, 7),
                new ThunderstormAbility(0.7),
                new RegenAbility()
        );
    }

    @Override
    public void onPreSpawn(Location spawnLocation, SpawnBuilder builder) {
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_TNT_PRIMED, 1, 1);
        builder.addDelay(25);
    }

    @Override
    public void onSpawn() {
        getMob().setDamageScalar(0.6); // only take 60% damage
        EntityEquipment equipment = getMob().getEntity().getEquipment();
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

    @Override
    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
        ItemUtils.giveOrDrop(killer, location, TROPHY);
    }
}
