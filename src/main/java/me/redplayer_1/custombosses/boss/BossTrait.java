package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.events.MainListener;
import me.redplayer_1.custombosses.util.LocationUtils;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;
import java.util.UUID;

public class BossTrait extends Trait {
    Boss parentBoss;
    UUID bukkitUUID;
    DecimalFormat healthFormat = new DecimalFormat("0.00");
    Player closestPlayer = null;

    protected BossTrait(Boss parentBoss) {
        super("custombosstrait");
        this.parentBoss = parentBoss;
    }

    @Override
    public void run() {
        // ignore if entity is dead
        if (getNPC().getEntity() == null || getNPC().getEntity().isDead()) return;

        // choose a player to target (must be within range)
        closestPlayer = LocationUtils.getClosestPlayer(getNPC().getStoredLocation(), true, parentBoss.getConfig().getAttackRange());

        if (closestPlayer == null) {
            // there are no nearby players
            getNPC().getNavigator().cancelNavigation();
        } else {
            getNPC().getNavigator().setTarget(closestPlayer, true);
        }

        getNPC().setName(parentBoss.getFormattedName(((LivingEntity) getNPC().getEntity()).getHealth()));
    }

    @Override
    public void onSpawn() {
        //store the boss's uuid in its entity's metadata
        bukkitUUID = getNPC().getEntity().getUniqueId();
        getNPC().getEntity().setMetadata(Boss.UUID_METADATA_KEY, new FixedMetadataValue(CustomBosses.getInstance(), parentBoss.getEntity().getUniqueId().toString()));

        // create a task to use the boss's abilities every so often
        if (!parentBoss.getAbilities().isEmpty()) {
            getNPC().getEntity().getScheduler().runAtFixedRate(CustomBosses.getInstance(),
                    scheduledTask -> {
                        if (!parentBoss.getEntity().isSpawned()) {
                            scheduledTask.cancel();
                            return;
                        }
                        try {
                            parentBoss.useAbility();
                        } catch (NullPointerException e) {
                            scheduledTask.cancel();
                        }
                    },
                    null, BossAbility.DEFAULT_USAGE_DELAY, BossAbility.DEFAULT_USAGE_DELAY);
        }
    }

    @Override
    public void onDespawn(DespawnReason reason) {
        if (reason == DespawnReason.DEATH) {
            parentBoss.kill(MainListener.getLastDamager(bukkitUUID));
            MainListener.removeLastDamager(bukkitUUID);
        } else {
            parentBoss.despawn();
        }
    }
}
