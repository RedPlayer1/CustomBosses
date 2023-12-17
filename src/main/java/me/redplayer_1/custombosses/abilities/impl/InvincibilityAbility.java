package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class InvincibilityAbility extends BossAbility {
    private static final HashSet<Boss> invincibleBosses = new HashSet<>();
    private static final int TICK_DURATION = 75;
    private static final int TICK_PERIOD = 5;
    private static final short RADIUS = 1;
    //private final DamageListener listener = new DamageListener();

    static {
        Bukkit.getPluginManager().registerEvents(new DamageListener(), CustomBosses.getInstance());
    }

    public InvincibilityAbility() {
        super("<dark_green>Invincibility", true, 0.7);
    }

    @Override
    public BossAbility newInstance() {
        return new InvincibilityAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        if (boss.getLocation() == null) return false;

        AtomicInteger tickCount = new AtomicInteger();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GREEN, 1);

        invincibleBosses.add(boss);
        // add slowness to make the particles look better
        ((LivingEntity) boss.getMob().getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TICK_DURATION * 20, 10, false, false));

        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), task -> {
            if (tickCount.get() > TICK_DURATION || boss.getMob().isDead()) {
                invincibleBosses.remove(boss);
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

            // 10 ticks = 1/2 second (@20 TPS)
            tickCount.set(tickCount.get() + TICK_PERIOD);
        }, 1, TICK_PERIOD);

        return true;
    }

    public static boolean isInvincible(Boss boss) {
        return invincibleBosses.contains(boss);
    }
}

class DamageListener implements Listener {
    // listener to stop any damage to invincible bosses
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Boss boss = Boss.of(event.getEntity());
        if (boss == null) return;

        if (InvincibilityAbility.isInvincible(boss)) {
            event.setCancelled(true);
        }
    }
}
