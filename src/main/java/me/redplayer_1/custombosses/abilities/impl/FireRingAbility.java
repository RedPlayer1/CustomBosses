package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FireRingAbility extends BossAbility {
    private static final int FIRE_TICKS = 120;

    public FireRingAbility() {
        this(0.65);
    }

    public FireRingAbility(double chance) {
        super("<gradient:#ff7300:#ff4800>Fire Ring</gradient>", true, chance);
    }

    @Override
    public BossAbility newInstance() {
        return new FireRingAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        Location bossLoc = boss.getLocation();
        if (bossLoc == null) return false;
        int radius = 5;

        // spawn a ring of fire around the boss
        for (int degree = 0; degree < 360; degree++) {
            double radians = Math.toRadians(degree);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;
            bossLoc.getWorld().spawnParticle(Particle.FLAME, bossLoc.clone().add(x, 0, z), 10, 0, 0, 0, 0);
        }

        // set all living entities inside circle on fire
        for (Player player : bossLoc.getNearbyPlayers(radius)) {
            player.playSound(player, Sound.ITEM_FIRECHARGE_USE, 1, 0);
            player.setFireTicks(FIRE_TICKS);
        }

        return true;
    }
}
