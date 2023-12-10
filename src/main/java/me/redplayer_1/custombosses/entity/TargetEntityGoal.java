package me.redplayer_1.custombosses.entity;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class TargetEntityGoal implements Goal<Mob>, Listener {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, new NamespacedKey(CustomBosses.getInstance(), "target_entity_goal"));
    private Mob owner;
    private LivingEntity target;
    private int attackRange;
    private boolean hostile;

    public TargetEntityGoal(Mob owner, int attackRange, boolean hostile) {
        this.owner = owner;
        this.hostile = hostile;
    }

    public TargetEntityGoal(Mob owner, int attackRange, boolean hostile, LivingEntity target) {
        this(owner, attackRange, hostile);
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
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        if (!hostile) return;
        if (target == null || !isValidTarget(target)) {
            // target nearest entity
            LivingEntity entity = owner.getLocation().getNearbyLivingEntities(attackRange).stream().toList().get(0);
            if (entity != null) {
                owner.setTarget(entity);
            }
        }
    }

    private boolean isValidTarget(LivingEntity target) {
        if (!target.isValid() || target.equals(owner) || !target.getWorld().equals(owner.getWorld())) return false;
        if (target instanceof Player p) return p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR;
        return true;
    }

    public void setTarget(LivingEntity target) {
        if (isValidTarget(target)) this.target = target;
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() {
        return KEY;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {

    }
}
