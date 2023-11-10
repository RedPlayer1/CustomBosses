package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class InvincibilityAbility extends BossAbility {
    private static final int TICK_DURATION = 75;

    static {
        // create event listener
    }

    public InvincibilityAbility() {
        super("<dark_green>Invincibility", true, 0.4);
    }

    @Override
    public BossAbility newInstance() {
        return new InvincibilityAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        AtomicInteger tickCount = new AtomicInteger();
        final double startingHealth = ((LivingEntity) boss.getEntity().getEntity()).getHealth();

        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), task -> {
            // cancel if necessary
            if (tickCount.get() > TICK_DURATION || !boss.getEntity().isSpawned()) {
                task.cancel();
                return;
            }

            //Bukkit.getServer().getPluginManager().event

            LivingEntity bossEntity = (LivingEntity) boss.getEntity().getEntity();
            if (bossEntity.getHealth() < startingHealth) {
                bossEntity.setHealth(startingHealth);
            }

            tickCount.set(tickCount.get() + 1);
        }, 0, 1);

        return true;
    }
}
