package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MinionAbility extends CooldownBossAbility {
    private final String name = "<gradient:#73ff00:#83ff6e>Minion</gradient>";

    @Override
    public boolean use(Boss boss, Player target) {
        Location loc = boss.getLocation();
        for (int i = 0; i < 5; i++) {
            // give the minions fire resistance so they don't burn during the day
            LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1, false, false));
            entity.customName(MessageUtils.miniMessageToComponent(name));
            entity.setCustomNameVisible(true);
        }
        return true;
    }

    @Override
    public boolean isSingleTarget() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getCooldown() {
        return 120; // 6 seconds
    }

    @Override
    public double getChance() {
        return 0.1; // 10% chance
    }
}
