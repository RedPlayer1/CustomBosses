package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.Player;

public class ThunderstormAbility implements BossAbility {
    @Override
    public boolean use(Boss boss, Player target) {
        target.getLocation().getWorld().strikeLightning(target.getLocation());
        return true;
    }

    @Override
    public String getName() {
        return "<gradient:yellow:gold>Thunderstorm</gradient>";
    }

    @Override
    public boolean isSingleTarget() {
        return false;
    }

    @Override
    public double getChance() {
        return 0.4;
    }
}
