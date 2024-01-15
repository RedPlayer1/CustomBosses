package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class RegenAbility extends CooldownBossAbility {
    private static final int INCREMENTS = 6;
    private static final int PERIOD = 15;

    public RegenAbility() {
        this(0.85);
    }

    public RegenAbility(double chance) {
        super("<gradient:red:dark_red>Regen</gradient>", true, chance, PERIOD * INCREMENTS);
    }

    @Override
    public boolean use(BossEntity bossEntity, Player target) {
        AtomicInteger count = new AtomicInteger();
        final double incrAmount = Math.min(bossEntity.getMob().getMaxHealth() * 0.08, 1);

        // can't use regen effect because the entity's health is always the same
        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), task -> {
            if (count.getAndIncrement() < INCREMENTS && !bossEntity.getMob().isDead())
                bossEntity.getMob().setHealth(bossEntity.getMob().getHealth() + incrAmount);
        }, 0, PERIOD);

        return true;
    }
}
