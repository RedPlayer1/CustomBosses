package me.redplayer_1.custombosses.boss.impl;

import me.redplayer_1.custombosses.abilities.impl.RegenAbility;
import me.redplayer_1.custombosses.abilities.impl.ThunderstormAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossType;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasicZombie extends Boss {
    private static ItemStack TROPHY = new ItemStack(Material.ZOMBIE_HEAD);

    static {
        ItemMeta meta = TROPHY.getItemMeta();
        meta.displayName(MessageUtils.mmsgToComponent("<gradient:dark_green:#4dff00>Basic Zombie Trophy</gradient>"));
        meta.lore(List.of(MessageUtils.mmsgToComponent("<gray>Obtained from killing a <dark_green>Basic Zombie</dark_green>")));
        TROPHY.setItemMeta(meta);
    }

    public BasicZombie() {
        super(new BossConfig(
                "ZOMBIE", BossType.BASIC_ZOMBIE, "Basic Zombie", 50, 15),
                new RegenAbility(), new ThunderstormAbility()
        );
    }

    @Override
    public void onPreSpawn(Location spawnLocation) {
        spawnLocation.getWorld().spawnParticle(Particle.CLOUD, spawnLocation, 20);
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
        if (killer instanceof Player p) {
            for (ItemStack item : p.getInventory().addItem(TROPHY).values()) {
                location.getWorld().dropItemNaturally(location, item);
            }
        } else {
            location.getWorld().dropItemNaturally(location, TROPHY);
        }
    }


}
