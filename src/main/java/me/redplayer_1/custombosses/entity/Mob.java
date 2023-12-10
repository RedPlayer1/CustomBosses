package me.redplayer_1.custombosses.entity;

import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class Mob {
    // The Mob's UUID is stored in their metadata as a String.
    public static final String METADATA_KEY = "custom_mob";
    private static HashMap<UUID, Mob> registry = new HashMap<>();
    private UUID uuid = UUID.randomUUID();
    private LivingEntity entity;
    /*
    About:
    - health (independent of entity)
    - type (bukkit mob/entity)
    - name (as holo = color support)
    - use paper goal api
     */

    public Mob(EntityType type, Location location, int attackRange, boolean hostile) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        if (entity instanceof LivingEntity) {
            entity.setMetadata(METADATA_KEY, new FixedMetadataValue(CustomBosses.getInstance(), uuid));
            Bukkit.getMobGoals().addGoal((org.bukkit.entity.Mob) entity, 0, new TargetEntityGoal((org.bukkit.entity.Mob) entity, attackRange, hostile));
        } else {
            throw new RuntimeException("The Mob {name} must be a type of LivingEntity! (not " + type.name() + ")");
        }
    }

    public static boolean isMob(LivingEntity entity) {
        return entity.hasMetadata(METADATA_KEY);
    }

    public static @Nullable Mob fromBukkit(LivingEntity entity) {
        MetadataValue value = entity.getMetadata(METADATA_KEY).get(0);
        if (value != null) {
            return registry.get(UUID.fromString(value.asString()));
        }
        return null;
    }
}
