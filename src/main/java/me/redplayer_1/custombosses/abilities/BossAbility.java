package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class BossAbility {
    // the number of ticks to wait before trying to use an ability
    public static final long DEFAULT_USAGE_DELAY = 60L;
    // Format w/ boss name, ability name, target name
    public static final String USAGE_MESSAGE = "<red>%s</red> used %s";
    public static final Set<BossAbility> ABILITIES = new HashSet<>();

    private final String name;
    private final boolean singleTarget;
    private final double chance;

    public BossAbility(String name, boolean singleTarget, double chance) {
        this.name = name;
        this.singleTarget = singleTarget;
        this.chance = chance;
        ABILITIES.add(this);
    }

    /**
     * Fired when an entity attacks and this ability was chosen
     *
     * @param bossEntity the boss using the ability
     * @param target     the player the ability is being used on
     * @return whether the ability was activated successfully (if not, a new one is chosen)
     */
    public abstract boolean use(BossEntity bossEntity, Player target);

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
