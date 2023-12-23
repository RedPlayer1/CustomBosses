package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class RegenAbility extends CooldownBossAbility {
    private static final int INCREMENTS = 6;
    private static final int PERIOD = 15;

    public RegenAbility() {
        super("<gradient:red:dark_red>Regen</gradient>", true, 0.85, PERIOD * INCREMENTS);
    }

    @Override
    public BossAbility newInstance() {
        return new RegenAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        AtomicInteger count = new AtomicInteger();
        final double incrAmount = Math.min(boss.getMob().getMaxHealth() * 0.08, 1);

        // can't use regen effect because the entity's health is always the same
        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), task -> {
            if (count.getAndIncrement() < INCREMENTS && !boss.getMob().isDead())
                boss.getMob().setHealth(boss.getMob().getHealth() + incrAmount);
        }, 0, PERIOD);

        return true;
    }
}
