package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.CustomBosses;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.text.DecimalFormat;
import java.util.Objects;

public class BossTrait extends Trait {
    Boss parentBoss;
    DecimalFormat healthFormat = new DecimalFormat("0.00");
    Player closestPlayer = null;
    double maxAttackRange = 10; // in blocks

    protected BossTrait(Boss parentBoss) {
        super("custombosstrait");
        this.parentBoss = parentBoss;
    }

    @Override
    public void run() {
        // ignore if entity is dead
        if (getNPC().getEntity() == null) return;
        if (getNPC().getEntity().isDead()) {
            parentBoss.despawn();
            return;
        }
        // choose a player to target (must be within range)
        if (closestPlayer == null || getNPC().getStoredLocation().distanceSquared(closestPlayer.getLocation()) > maxAttackRange) {
            closestPlayer = getClosestPlayer(getNPC().getStoredLocation());
        }
        getNPC().getNavigator().setTarget(closestPlayer, true);
        getNPC().setName(parentBoss.getFormattedName(((LivingEntity) getNPC().getEntity()).getHealth()));
    }

    private Player getClosestPlayer(Location loc) {
        Player closest = null;
        for (Player p : loc.getWorld().getPlayers()) {
            if (closest == null) closest = p;
            if (loc.distanceSquared(p.getLocation()) < loc.distanceSquared(closest.getLocation())) {
                closest = p;
            }
        }
        return closest;
    }

    @Override
    public void onSpawn() {
        //store the boss's uuid in its entity's metadata
        getNPC().getEntity().setMetadata(Boss.UUID_METADATA_KEY, new FixedMetadataValue(CustomBosses.getInstance(), parentBoss.getEntity().getUniqueId().toString()));
    }

    @Override
    public void onDespawn(DespawnReason reason) {
        // bosses shouldn't persist after server reload/restart
        if (reason == DespawnReason.DEATH) {
            EntityDamageEvent damageCause = npc.getEntity().getLastDamageCause();
            if (damageCause == null || !(damageCause.getEntity() instanceof LivingEntity)) {
                // TODO: anonymous death broadcast (if enabled)
                parentBoss.onKill(null);
            } else {
                // TODO: entity death broadcast (if enabled)
                parentBoss.onKill((LivingEntity) damageCause.getEntity());
            }
        }
        parentBoss.despawn();
    }
}
