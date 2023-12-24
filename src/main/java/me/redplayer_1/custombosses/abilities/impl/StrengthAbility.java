package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthAbility extends CooldownBossAbility {
    private static final int DURATION = 20 * 5;
    private int amplifier = 2;

    public StrengthAbility() {
        this(0.6);
    }

    public StrengthAbility(double chance) {
        super("<gradient:red:dark_red>Strength<gradient>", true, chance, DURATION);
    }

    public StrengthAbility(double chance, int amplifier) {
        this(chance);
        this.amplifier = amplifier;
    }

    @Override
    public BossAbility newInstance() {
        return new FireRingAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        boss.getMob().getEntity().addPotionEffect(
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE, DURATION, amplifier, false, true)
        );
        return true;
    }
}
