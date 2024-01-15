package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.boss.Trophy;
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

public class BasicHusk implements Boss {
    private static final ItemStack TROPHY = new Trophy(
            Material.DEAD_BUSH,
            "<gradient:#ff5c26:yellow>Basic Husk Trophy</gradient>",
            List.of("<gray>Obtained from killing a <color:#ff5c26>Basic Husk</color>")
    );

    /*
    public BasicHusk() {
        super(
                new BossConfig("HUSK", BossType.BASIC_HUSK, "Basic Husk", 100, 20),
                1,
                new FireRingAbility(0.4),
                new StrengthAbility(0.9, 7),
                new ThunderstormAbility(0.7),
                new RegenAbility()
        );
    }

     */

    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) {
        spawnLocation.getWorld().playSound(spawnLocation, Sound.ENTITY_TNT_PRIMED, 1, 1);
        builder.addDelay(25);
    }

    public void onSpawn(BossEntity boss) {
        boss.getMob().setDamageScalar(0.6); // only take 60% damage - FIXME
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
        ItemUtils.giveOrDrop(killer, location, TROPHY);
    }
}
