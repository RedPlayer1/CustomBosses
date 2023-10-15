package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.Player;

public interface BossAbility {

    /**
     * Fired when an entity attacks and this ability was chosen
     * @param boss the boss using the ability
     * @param target the player the ability is being used on
     */
    void use(Boss boss, Player target);

    /**
     * Specifies how long the cooldown is
     *
     * @return the cooldown, in ticks (20 tps)
     */
    default long getCooldown() {
        return 40;
    }

    /**
     * Specifies the chance for this ability to be used
     * @return the chance as a decimal
     */
    default double getChance() {
        return 0.10; // a 10% chance
    }
}
