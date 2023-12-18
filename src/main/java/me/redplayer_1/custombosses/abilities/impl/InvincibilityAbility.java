package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicInteger;

public class InvincibilityAbility extends CooldownBossAbility {
    private static final int TICK_DURATION = 75;
    private static final int TICK_PERIOD = 5;
    private static final short RADIUS = 1;

    public InvincibilityAbility() {
        super("<dark_green>Invincibility", true, 0.6, TICK_DURATION);
    }

    @Override
    public BossAbility newInstance() {
        return new InvincibilityAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        AtomicInteger tickCount = new AtomicInteger();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GREEN, 1);

        boss.getMob().setInvincible(TICK_DURATION);
        // add slowness to make the particles look better
        boss.getMob().getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TICK_DURATION, 10, false, false));

        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), task -> {
            if (tickCount.get() > TICK_DURATION || boss.getMob().isDead()) {
                task.cancel();
                return;
            }
            // create a double helix
            Location loc = boss.getLocation();
            for (double y = 0; y < 3; y += 0.5) {
                double x = RADIUS * Math.cos(tickCount.get() + y);
                double z = RADIUS * Math.sin(tickCount.get() + y);
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc.x() + x, loc.y() + y, loc.z() + z, 5, dustOptions);
                loc.getWorld().spawnParticle(Particle.REDSTONE, -1 * x + loc.x(), loc.y() + y, -1 * z + loc.z(), 5, dustOptions);
            }

            tickCount.set(tickCount.get() + TICK_PERIOD);
        }, 0, TICK_PERIOD);

        return true;
    }
}
