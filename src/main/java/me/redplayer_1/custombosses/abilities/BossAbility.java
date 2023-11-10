package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.boss.Boss;
import org.bukkit.entity.Player;

// TODO: ability config?
public abstract class BossAbility {

    // the number of ticks to wait before trying to use an ability
    public static final long DEFAULT_USAGE_DELAY = 60L;

    // Format w/ boss name, ability name, target name
    public static final String USAGE_MESSAGE = "<red>%s</red> used %s";

    private String name;
    private boolean singleTarget;
    private double chance;

    public BossAbility(String name, boolean singleTarget, double chance) {

    }

    public abstract BossAbility newInstance();

    /**
     * Fired when an entity attacks and this ability was chosen
     *
     * @param boss   the boss using the ability
     * @param target the player the ability is being used on
     * @return whether the ability was activated successfully (if not, a new one is chosen)
     */
    public abstract boolean use(Boss boss, Player target);

    /**
     * Whether the ability should be used on all surrounding entities or just one
     *
     * @return if the ability will target 1 or 1+ entities
     */
    public boolean isSingleTarget() {
        return singleTarget;
    }

    /**
     * The name of the ability. This can include MiniMessage delimiters
     *
     * @return the player-friendly name of this ability
     */
    public String getName() {
        return name;
    }

    /**
     * Specifies the chance for this ability to be used. (ex. 0.40 == 40%, 1.00 = 100%, 0.15 = 15%)
     *
     * @return the chance as a decimal
     */
    public double getChance() {
        return chance;
    }
}
