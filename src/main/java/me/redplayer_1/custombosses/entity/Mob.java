package me.redplayer_1.custombosses.entity;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.events.DamageListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class Mob implements Listener {
    // The Mob's UUID is stored in their metadata as a String.
    public static final String METADATA_KEY = "custom_mob";
    private static final HashMap<UUID, Mob> registry = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();

    // incoming damage is multiplied by this. Affected by custom armor & weapons (+ enchants)
    private double damageScalar = 1.0;

    private String name;
    private final LivingEntity entity;
    private boolean invincible;
    private double maxHealth;
    private double health; // the actual health of this mob
    private boolean showHealth;

    // health milestones (Good = green | Medium = yellow | Bad = red)
    private double healthMedium; // < 60% max hp
    private double healthBad; // < 20% max hp

    private final Hologram hologram; // line 0 is the name, line 1 is the health
    private Consumer<Hologram> hologramManager; // function to manage the hologram shown above the entity

    /**
     * Create a new {@link Mob} that is automatically spawned.
     * @param name the display name (can contain color codes)
     * @param type type of mob to spawn (Must be a {@link LivingEntity})
     * @param location the Location to spawn the Mob at
     * @param maxHealth the maximum health
     * @param attackRange how far away entities can be attacked form (distance in blocks ^ 2)
     * @param hostile whether the Mob should attack other entities
     * @param invincible if the Mob should take damage
     */
    public Mob(String name, EntityType type, Location location, double maxHealth, boolean showHealth, double attackRange, boolean hostile, boolean invincible) {
        this(name, type, location, maxHealth, showHealth, attackRange, hostile);
        this.invincible = invincible;
    }

    /**
     * Create a new {@link Mob} that is automatically spawned.
     * @param name the display name (can contain color codes)
     * @param type type of mob to spawn (Must be a {@link LivingEntity})
     * @param location the Location to spawn the Mob at
     * @param maxHealth the maximum health
     * @param attackRange how far away entities can be attacked form (distance in blocks ^ 2)
     * @param hostile whether the Mob should attack other entities
     */
    public Mob(String name, EntityType type, Location location, double maxHealth, boolean showHealth, double attackRange, boolean hostile) {
        // ensure entity is a living one
        if (location.getWorld().spawnEntity(location, type) instanceof LivingEntity le) {
            entity = le;
            entity.setMetadata(METADATA_KEY, new FixedMetadataValue(CustomBosses.getInstance(), uuid));
            Bukkit.getMobGoals().addGoal((org.bukkit.entity.Mob) entity, 0, new TargetEntityGoal((org.bukkit.entity.Mob) entity, attackRange, null, hostile));

        } else {
            throw new RuntimeException("The Mob {name} must be a type of LivingEntity! (not " + type.name() + ")");
        }
        registry.put(uuid, this);
        this.name = name;
        setMaxHealth(maxHealth);
        this.health = maxHealth;
        this.showHealth = showHealth;
        hologram = DHAPI.createHologram(uuid.toString(), entity.getLocation(), false);

        Bukkit.getPluginManager().registerEvents(this, CustomBosses.getInstance());

        // default Hologram Manager
        setHologramManager((hologram) -> {
            DHAPI.setHologramLine(hologram, 0, name);
            if (showHealth)
                try {
                    String color = "&a";
                    if (health < healthBad) color = "&4";
                    else if (health < healthMedium) color = "&6";
                    DHAPI.setHologramLine(hologram, 1, String.format("&l&c♡ &r" + color + "%.2f", health));
                } catch (IllegalArgumentException ex) {
                    DHAPI.addHologramLine(hologram, "");
                }
            else if (hologram.getPage(0).getLines().size() > 1) {
                DHAPI.removeHologramLine(hologram, 1);
            }

            DHAPI.moveHologram(hologram, entity.getLocation().add(0, entity.getEyeHeight() + 1, 0));
        }, showHealth? 2 : 1);

        // moves nametag hologram every 10 ticks to reduce lag
        Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), (task) -> {
            if (entity.isDead() || health <= 0) {
                task.cancel();
            } else {
                hologramManager.accept(hologram);
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
        if (health - amount <= 0) {
            kill();
        } else {
            health -= amount;
        }
    }

    /**
     * Kills the Mob and its corresponding Entity
     * @return if the Mob was successfully killed
     */
    public boolean kill() {
        return !kill(false);
    }

    /**
     * Kills the Mob and its corresponding Entity
     * @param force if the Mob should die regardless of whether the {@link MobDeathEvent} was cancelled
     * @return if the event was cancelled
     */
    public boolean kill(boolean force) {
        double prevHealth = health;
        health = 0; // make isDead() return true
        MobDeathEvent event = new MobDeathEvent(this, DamageListener.getLastDamager(uuid));
        event.callEvent();
        if (force || !event.isCancelled()) {
            HandlerList.unregisterAll(this);
            entity.setHealth(0);
            hologram.delete();
        } else {
            health = prevHealth;
        }
        return event.isCancelled();
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

    /**
     * @return the Bukkit representation of the entity (health changes not reflected, @see )
     */
    public LivingEntity getEntity() {
        return entity;
    }

    public String getName() {
        return name;
    }

    /**
     * @param name the name to set (supports Minecraft color codes)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the health of the Mob. Values greater than the max will be rounded down.
     */
    public void setHealth(double health) {
        setHealth(health, false);
    }

    /**
     * Sets the health of the Mob. Values greater than the max can be used by setting ignoreMax to true.
     * @param ignoreMax whether values greater than the maximum should be allowed
     */
    public void setHealth(double health, boolean ignoreMax) {
        if (ignoreMax) this.health = health;
        else this.health = Math.min(health, maxHealth);
    }

    public double getHealth() {
        return health;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        healthMedium = maxHealth * .6;
        healthBad = maxHealth * .2;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setDamageScalar(double damageScalar) {
        this.damageScalar = damageScalar;
    }

    public double getDamageScalar() {
        return damageScalar;
    }

    public void setShowHealth(boolean showHealth) {
        this.showHealth = showHealth;
    }

    public boolean isShowingHealth() {
        return showHealth;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    /**
     * @param manager Function that is called every few ticks to handle the changing of the Hologram
     *                shown above the entity.
     * @param lines the number of lines to be put on the first page of the Hologram
     */
    public void setHologramManager(Consumer<Hologram> manager, int lines) {
        if (hologram.getPage(0).getLines().size() != lines) {
            hologram.removePage(0);
            hologram.addPage().setIndex(0);
            for (int i = 0; i < lines; i++) {
                DHAPI.addHologramLine(hologram, String.valueOf(i));
            }
        }
        hologramManager = manager;
    }

    /**
     * Sets {@link Mob#invincible} to the value specified for the duration, then reverts it back.
     * @param invincible value to set
     * @param ticks how long to set it for (in ticks)
     */
    public void setInvincible(boolean invincible, long ticks) {
        boolean previous = this.invincible;
        this.invincible = invincible;
        Bukkit.getScheduler().runTaskLater(CustomBosses.getInstance(), () -> setInvincible(previous), ticks);
    }

    /**
     * Makes the Mob invincible for the duration and then makes it vincible again.
     * @param ticks how long the Mob should be invincible (in ticks)
     */
    public void setInvincible(long ticks) {
        invincible = true;
        Bukkit.getScheduler().runTaskLater(CustomBosses.getInstance(), () -> invincible = false, ticks);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().equals(entity)) {
            // damage is affected by scalars, armor, and weapons
            if (!invincible)
                damage(event.getFinalDamage() * damageScalar);
            else event.setCancelled(true);
            event.setDamage(0);
        }
    }
}
