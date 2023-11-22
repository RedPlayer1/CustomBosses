package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RegenAbility extends BossAbility {

    public RegenAbility() {
        super("<gradient:red:dark_red>Regen</gradient>", true, 0.85);
    }

    @Override
    public BossAbility newInstance() {
        return new RegenAbility();
    }

    @Override
    public boolean use(Boss boss, Player target) {
        ((LivingEntity) boss.getEntity().getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4, 3, true, true));
        return true;
    }
}
