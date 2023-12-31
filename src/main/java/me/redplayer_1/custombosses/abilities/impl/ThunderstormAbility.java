package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.Player;

public class ThunderstormAbility extends BossAbility {
    public ThunderstormAbility() {
        this(0.6);
    }

    public ThunderstormAbility(double chance) {
        super("<gradient:yellow:gold>Thunderstorm</gradient>", false, chance);
    }

    @Override
    public boolean use(Boss boss, Player target) {
        boss.getMob().setInvincible(true, 15); // lightning shouldn't damage boss
        target.getLocation().getWorld().strikeLightning(target.getLocation());
        return true;
    }
}
