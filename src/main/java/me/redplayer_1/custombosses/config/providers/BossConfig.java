package me.redplayer_1.custombosses.config.providers;

import me.redplayer_1.custombosses.abilities.Abilities;
import me.redplayer_1.custombosses.api.Boss;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.boss.Trophy;
import me.redplayer_1.custombosses.config.Config;
import me.redplayer_1.custombosses.util.CommandSequence;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class for the (de)serialization of {@link BossEntity Bosses}
 */
public final class BossConfig {
    private static final HashMap<String, BossConfig> TYPES = new HashMap<>();

    private EntityType entityType;
    private String bossType; // command friendly name for the boss
    private String displayName; // has color codes
    private String plainName; // displayName w/out color mc color codes
    private double health;
    private double damageScalar;
    private double attackRange;
    private @Nullable CommandSequence preSpawnSequence;
    private @Nullable CommandSequence spawnSequence;
    private @Nullable CommandSequence killSequence;
    private @Nullable Trophy trophy;
    private @Nullable Boss boss;
    private List<Abilities> abilities = new ArrayList<>();

    /**
     * @param attackRange range  that the boss will target/chase a player
     */
    public BossConfig(String displayName, EntityType entityType, double health, double attackRange, Abilities... abilities) {
        this(displayName);
        this.entityType = entityType;
        this.health = health;
        this.attackRange = attackRange;
        this.abilities = new ArrayList<>(Arrays.asList(abilities));
    }

    private BossConfig(String displayName) {
        setDisplayName(displayName);
    }

    /** Copy constructor */
    private BossConfig(EntityType entityType, String bossType, String displayName, String plainName, double health, double damageScalar, double attackRange, @Nullable Boss boss, List<Abilities> abilities) {
        this.entityType = entityType;
        this.bossType = bossType;
        this.displayName = displayName;
        this.plainName = plainName;
        this.health = health;
        this.damageScalar = damageScalar;
        this.attackRange = attackRange;
        this.boss = boss;
        this.abilities = abilities;
    }

    /**
     * Load Boss types from a Config. A Boss cannot be spawned until loaded by this method.
     *
     * @param config the config to analyze
     */
    public static void loadFrom(Config config) {
        for (String name : config.getConfig().getKeys(false)) {
            ConfigurationSection section = config.getConfig().getConfigurationSection(name);
            BossConfig bossConfig = new BossConfig(name);
            if (TYPES.containsKey(bossConfig.bossType)) continue; // ensure all defined types are unique
            // see the wiki for more information

            String classpath = section.getString("classpath");
            if (classpath != null) {
                try {
                    Class<?> clazz = Class.forName(classpath);
                    if (Arrays.stream(clazz.getInterfaces()).anyMatch(c -> c.isInterface() && c.getSimpleName().equals("Boss")))
                        bossConfig.boss = (Boss) clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Error loading classpath of " + bossConfig.bossType + ":\n" + e.getMessage());
                }
            }
            try {
                // required values
                getValue("entity_type", section::getString, type -> {
                        try {
                            bossConfig.entityType = EntityType.valueOf(type);
                        } catch (IllegalArgumentException e) {
                            Bukkit.getLogger().warning("Invalid entity_type for Boss \"" + bossConfig.bossType + "\"");
                        }
                });
                getValue("health", section::getDouble, health -> {
                        if (health > 0)
                            bossConfig.health = health;
                        else {
                            Bukkit.getLogger().warning("Health must be > 0 (in " + bossConfig.bossType + " config)");
                        }
                });
                getValue("attack_range", section::getString, range -> bossConfig.attackRange = Double.parseDouble(range));
                getValue("abilities", section::getConfigurationSection, aSection -> {
                    for (String abilityName : aSection.getKeys(false)) {
                        if (abilityName.equalsIgnoreCase("none")) break;
                        Abilities ability = Abilities.valueOf(abilityName.toUpperCase());
                        ability.setChance(aSection.getDouble(abilityName));
                        bossConfig.abilities.add(ability);
                    }
                });

            } catch (ConfigValueNotFoundException e) {
                e.logError(bossConfig.bossType);
                return;
            }
            // optional values
            getValue("damage_scalar", key -> section.getDouble(key, 1), scalar -> bossConfig.damageScalar = scalar);

            // get trophy
            try {
                getValue("trophy", section::getConfigurationSection, tSection ->
                        getValue("material", str -> Material.valueOf(tSection.getString(str)), material ->
                                getValue("name", tSection::getString, tName ->
                                        getValue("lore", tSection::getStringList, lore ->
                                                        bossConfig.trophy = new Trophy(material, tName, lore), () ->
                                                bossConfig.trophy = new Trophy(material, tName, null)))));
            } catch (ConfigValueNotFoundException e) {
                e.logError(bossConfig.bossType + "/trophy");
            }

            // get event commands (see wiki for event-specific placeholders)
            getValue("events", section::getConfigurationSection, eSection -> {
                // TODO: add placeholders
                getValue("pre_spawn", eSection::getStringList, preSpawn -> {
                    CommandSequence cmdSequence = new CommandSequence();
                    for (String cmd : preSpawn) {
                        if (cmd.startsWith("delay")) {
                            try {
                                cmdSequence.addDelay(Integer.parseUnsignedInt(cmd.split(":")[1]));
                            } catch (NumberFormatException e) {
                                Bukkit.getLogger().warning("Invalid integer @" + bossConfig.bossType + "/events/pre_spawn (" + cmd + ")");
                            }
                        } else {
                            cmdSequence.addCommand(cmd);
                        }
                    }
                    bossConfig.preSpawnSequence = cmdSequence;
                }, () -> {});

                getValue("spawn", eSection::getStringList,
                        spawn -> bossConfig.spawnSequence = new CommandSequence(spawn),
                        () -> {}
                );

                getValue("kill", eSection::getStringList,
                        kill -> bossConfig.killSequence = new CommandSequence(kill),
                        () -> {}
                );
            });

            TYPES.put(bossConfig.bossType, bossConfig);
        }
    }

    /**
     * Attempt to get & apply values or throw error if null
     * @param key the yaml key for the value to get
     * @param getter the getter function for the key
     * @param setter the setter function for using the key's value
     * @param <T> type of the key's value
     */
    private static <T> void getValue(String key, Function<String, T> getter, Consumer<T> setter) throws ConfigValueNotFoundException {
        T val = null;
        try {
            val = getter.apply(key);
        } catch (Exception ignored) {}

        if (val != null) {
            setter.accept(val);
        } else {
            throw new ConfigValueNotFoundException(key);
        }
    }

    /**
     * Get the value and execute different tasks if it is null or not
     * @param key key to get
     * @param getter the getter function for the key
     * @param setter the setter function using the key's value
     * @param ifNull run if the key is null or an error occurred
     */
    private static <T> void getValue(String key, Function<String, T> getter, Consumer<T> setter, Runnable ifNull) {
        try {
            getValue(key, getter, setter);
        } catch (ConfigValueNotFoundException e) {
            ifNull.run();
        }
    }

    /**
     * @return a Set of all the registered boss types
     * @see BossConfig#getType(String)
     */
    public static Set<String> getRegisteredTypes() {
        return TYPES.keySet();
    }

    /**
     * Returns the defined configuration for the boss type
     * @param bossType type of boss
     * @return the BossConfig for the boss, or null if it hasn't been registered
     * @see BossConfig#getBossType()
     */
    public static @Nullable BossConfig getType(String bossType) {
        return TYPES.get(bossType);
    }

    /**
     * @return the Boss interface that handles events for the BossEntity
     */
    public @Nullable Boss getBoss() {
        return boss;
    }

    /**
     * Runs the action if there is a Boss for this config
     * @param action the action to perform
     */
    public void doIfBoss(Consumer<Boss> action) {
        if (boss != null) action.accept(boss);
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public double getHealth() {
        return health;
    }

    public double getAttackRange() {
        return attackRange;
    }

    /**
     * @return the display name (has color codes & whitespace)
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @see BossConfig#getPlainName()
     * @return the boss type (Boss's name if it were an enum)
     */
    public String getBossType() {
        return bossType;
    }

    /**
     * @see BossConfig#getDisplayName()
     * @return the display name without any color codes
     */
    public String getPlainName() {
        return plainName;
    }

    public double getDamageScalar() {
        return damageScalar;
    }

    public List<Abilities> getAbilities() {
        return abilities;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        // remove minecraft color codes
        plainName = displayName.replaceAll("[&§].", "");
        // remove whitespace
        bossType = plainName.replaceAll("\\s+", "_").toUpperCase();
    }

    private static class ConfigValueNotFoundException extends RuntimeException {
        private final String key;

        public ConfigValueNotFoundException(String key) {
            this.key = key;
        }

        public void logError(String bossType) {
            Bukkit.getLogger().warning("Required value \"" + key + "\" not found in configuration for " + bossType);
        }
    }
}