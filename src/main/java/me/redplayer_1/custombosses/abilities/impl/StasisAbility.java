package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.util.CachedList;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class StasisAbility extends CooldownBossAbility {
    private static final int DURATION = 4;
    private static final CachedList<Player> cache = new CachedList<>(DURATION, TimeUnit.SECONDS);

    public StasisAbility() {
        this(0.8);
    }
    public StasisAbility(double chance) {
        super("<gradient:green:yellow>Stasis</gradient>", false, chance, DURATION * 20 * 2 /* this ability is very op */);
    }

    @Override
    public BossAbility newInstance() {
        return new StasisAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, DURATION * 20, 69, false, true, false));
        cache.add(target);
        // TODO: add particle (helix/sphere around affected players)
        return true;
    }

    @SuppressWarnings("deprecation")
    public static boolean handleMove(Player p) {
        return !p.isOnGround() && cache.contains(p);
    }
}
