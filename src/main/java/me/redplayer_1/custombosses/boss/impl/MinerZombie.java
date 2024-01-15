package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.boss.Trophy;
import me.redplayer_1.custombosses.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MinerZombie implements Boss {
    private static final ItemStack TROPHY = new Trophy(
            Material.SEA_LANTERN,
            "<dark_green>Miner Zombie Trophy</dark_green>",
            List.of("<gray>Obtained from killing a <dark_green><i>Miner Zombie</i></dark_green>")
    );

    /*
    public MinerZombie() {
        super(new BossConfig(
                "ZOMBIE", BossType.MINER_ZOMBIE, "Miner Zombie", 150, 20),
                1,
                new RegenAbility(0.4),
                new StasisAbility(0.9),
                new MinionAbility(0.89),
                new InvincibilityAbility(0.7)
        );
    }
*/
    public void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder) {

    }

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

    public void onKill(@NotNull Location location, @Nullable LivingEntity killer) {
        ItemUtils.giveOrDrop(killer, location, TROPHY);
    }
}
