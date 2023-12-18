package me.redplayer_1.custombosses.entity;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class TargetEntityGoal implements Goal<Mob> {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, new NamespacedKey(CustomBosses.getInstance(), "target_entity_goal"));
    private final Mob parent;
    private LivingEntity target;
    private double attackRange; // will attack entities in range (blocks^2)
    private double targetRange = 600d; // will target entities in range (blocks^2)
    private final boolean defaultHostile; // if the entity type attacks players by default
    private boolean hostile;

    public TargetEntityGoal(Mob parent, double attackRange, @Nullable Double targetRange, boolean hostile) {
        this.parent = parent;
        this.attackRange = attackRange;
        if (targetRange != null) this.targetRange = targetRange;
        defaultHostile = parent instanceof Monster;
        this.hostile = hostile;
    }

    public TargetEntityGoal(Mob parent, double attackRange, @Nullable Double targetRange, boolean hostile, LivingEntity target) {
        this(parent, attackRange, targetRange, hostile);
        this.target = target;
    }

    public void setAttacking(boolean shouldAttack) {
        this.hostile = shouldAttack;
    }

    public boolean isAttacking() {
        return hostile;
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }

     @Override
    public void tick() {
        if (!hostile) return;
        if (target == null || !isValidTarget(target)) {
            // target nearest entity
            LivingEntity entity = parent.getLocation().getNearbyLivingEntities(targetRange).stream().toList().get(0);
            if (entity != null && !entity.equals(parent)) {
                target = entity;
                parent.setTarget(entity);
            }
        }
        if (!defaultHostile && parent.getLocation().distanceSquared(target.getLocation()) <= attackRange) {
            parent.attack(target);
        }
    }

    private boolean isValidTarget(LivingEntity target) {
        if (!target.isValid() || target.equals(parent) || !target.getWorld().equals(parent.getWorld())) return false;
        if (target instanceof Player p)
            return p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR;
        return true;
    }

    public void setTarget(LivingEntity target) {
        if (isValidTarget(target)) this.target = target;
    }

    public void setAttackRange(double attackRange) {
        this.attackRange = attackRange;
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() {
        return KEY;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }
}
