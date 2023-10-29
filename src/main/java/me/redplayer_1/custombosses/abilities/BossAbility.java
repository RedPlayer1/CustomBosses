package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.Player;

// TODO: ability config?
public interface BossAbility {

    // the number of ticks to wait before trying to use an ability
    long DEFAULT_USAGE_DELAY = 60L;

    // Format w/ boss name, ability name, target name
    String USAGE_MESSAGE = "<red>%s</red> used %s";

    /**
     * Fired when an entity attacks and this ability was chosen
     *
     * @param boss   the boss using the ability
     * @param target the player the ability is being used on
     * @return whether the ability was activated successfully (if not, a new one is chosen)
     */
    boolean use(Boss boss, Player target);

    /**
     * Whether the ability should be used on all surrounding entities or just one
     *
     * @return if the ability will target 1 or 1+ entities
     */
    boolean isSingleTarget();

    /**
     * The name of the ability. This can include MiniMessage delimiters
     *
     * @return the player-friendly name of this ability
     */
    String getName();

    /**
     * Specifies the chance for this ability to be used
     *
     * @return the chance as a decimal
     */
    default double getChance() {
        return 0.10; // a 10% chance
    }
}
