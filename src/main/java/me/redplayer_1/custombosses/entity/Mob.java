package me.redplayer_1.custombosses.entity;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Mob implements Listener {
    // The Mob's UUID is stored in their metadata as a String.
    public static final String METADATA_KEY = "custom_mob";
    private static final HashMap<UUID, Mob> registry = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();

    // incoming damage is multiplied by this. Affected by custom armor & weapons (+ enchants)
    private double naturalDamageScalar = 1.0;
    private double playerDamageScalar = 1.0;

    private LivingEntity entity;
    private final double maxHealth;
    private double health; // the actual health of this mob
    private Hologram hologram; // line 0 is the name, line 1 is the health
    /*
    About:
    - health (independent of entity)
    - type (bukkit mob/entity)
    - name (as holo = color support)
    - use paper goal api
     */

    // TODO: name may contain color codes?
    public Mob(String name, EntityType type, Location location, double maxHealth, int attackRange, boolean hostile) {
        // ensure entity is a living one
        if (location.getWorld().spawnEntity(location, type) instanceof LivingEntity le) {
            entity = le;
            entity.setMetadata(METADATA_KEY, new FixedMetadataValue(CustomBosses.getInstance(), uuid));
            Bukkit.getMobGoals().addGoal((org.bukkit.entity.Mob) entity, 0, new TargetEntityGoal((org.bukkit.entity.Mob) entity, attackRange, hostile));

        } else {
            throw new RuntimeException("The Mob {name} must be a type of LivingEntity! (not " + type.name() + ")");
        }
        registry.put(uuid, this);
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        Bukkit.getPluginManager().registerEvents(this, CustomBosses.getInstance());

        hologram = DHAPI.createHologram(uuid.toString(), entity.getLocation(), false, Arrays.asList("1", "2"));
        DHAPI.setHologramLine(hologram, 0, name);

        // moves nametag hologram every 10 ticks to reduce lag
        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), (task) -> {
            if (entity.isDead() || health <= 0) {
                task.cancel();
            } else {
                DHAPI.setHologramLine(hologram, 1, String.format("&l&c♡ &r%.2f", health));
                DHAPI.moveHologram(hologram, entity.getLocation().add(0, entity.getEyeHeight() + 1, 0));
            }
        }, 0, 5);
    }

    public static boolean isMob(LivingEntity entity) {
        return entity.hasMetadata(METADATA_KEY);
    }

    public static @Nullable Mob fromBukkit(LivingEntity entity) {
        if (!entity.hasMetadata(METADATA_KEY)) return null;
        MetadataValue value = entity.getMetadata(METADATA_KEY).get(0);
        if (value != null) {
            return registry.get(UUID.fromString(value.asString()));
        }
        return null;
    }

    /**
     * Damages the Mob
     * @param amount amount to damage (hit points)
     */
    public void damage(double amount) {
        health -= amount;
        if (health <= 0) {
            kill();
        }
    }

    /**
     * Kills the Mob and its corresponding Entity
     */
    public void kill() {
        HandlerList.unregisterAll(this);
        entity.setHealth(0);
        hologram.delete();
    }

    public boolean isDead() {
        return entity.isDead() || entity.getHealth() <= 0 || health <= 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return entity.getLocation();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(entity)) {
            // damage is affected by scalars, armor, and weapons
            if (event.getDamager() instanceof Player) {
                damage(event.getFinalDamage() * playerDamageScalar);
            } else {
                damage(event.getFinalDamage() * naturalDamageScalar);
            }
            event.setDamage(0);
        }
    }
}
