package me.redplayer_1.custombosses.api;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.config.Config;
import me.redplayer_1.custombosses.config.ConfigMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStats {
    private static Config globalConfig;
    private static final HashMap<UUID, PlayerStats> registry = new HashMap<>();
    private static final ConfigMap<String, Integer> globalKills = new ConfigMap<>(s -> s, i -> Integer.toString(i), s -> s, Integer::valueOf);
    private static final ConfigMap<String, Integer> globalSpawns = new ConfigMap<>(s -> s, i -> Integer.toString(i), s -> s, Integer::valueOf);
    private Config data;
    private final UUID uuid;
    private final ConfigMap<String, Integer> bossKills = new ConfigMap<>(s -> s, i -> Integer.toString(i), s -> s, Integer::valueOf);
    private final ConfigMap<String, Integer> bossSpawns = new ConfigMap<>(s -> s, i -> Integer.toString(i), s -> s, Integer::valueOf);

    static {
        // get global info from file
        try {
            globalConfig = new Config("GlobalStats");
            globalSpawns.loadFrom(globalConfig, "Spawns", new HashMap<>());
            globalKills.loadFrom(globalConfig, "Kills", new HashMap<>());
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Error whilst saving global data!");
        }
    }

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        try {
            // load data
            data = new Config("data" + File.separator + uuid);
            bossSpawns.loadFrom(data, "Spawns", new HashMap<>());
            bossKills.loadFrom(data, "Kills", new HashMap<>());

        } catch (IOException e) {
            Bukkit.getLogger().severe("IOException whilst loading data for player " + uuid + "!");
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Invalid syntax found in " + uuid + ".yml!");
        }

        registry.put(uuid, this);
    }

    /**
     * Increments the spawn counter for this type
     *
     * @param type the type of boss spawned
     */
    public void incrementSpawn(String type) {
        Integer val = bossSpawns.get(type);
        if (val == null) {
            bossSpawns.put(type, 1);
        } else {
            bossSpawns.put(type, val + 1);
        }
        val = globalSpawns.get(type);
        if (val == null) {
            globalSpawns.put(type, 1);
        } else {
            globalSpawns.put(type, val + 1);
        }
    }

    /**
     * Increments the kill counter for this type
     *
     * @param type the type of boss killed
     */
    public void incrementKill(String type) {
        Integer val = bossKills.get(type);
        if (val == null) {
            bossKills.put(type, 1);
        } else {
            bossKills.put(type, val + 1);
        }
        val = globalKills.get(type);
        if (val == null) {
            globalKills.put(type, 1);
        } else {
            globalKills.put(type, val + 1);
        }
    }

    /**
     * Attempt to save stats to disk
     *
     * @param remove whether this instance should be removed from the registry (only use on player logouts)
     */
    public void save(boolean remove) {
        // if everything is 0, don't save
        if (bossSpawns.getMap().values().isEmpty() && bossKills.getMap().values().isEmpty()) return;

        bossSpawns.saveTo(data, "Spawns");
        bossKills.saveTo(data, "Kills");
        data.save();

        if (remove) {
            registry.remove(uuid);
        }
    }

    /**
     * Saves all the player stats in the registry
     *
     * @param removeFromRegistry whether the registry should be cleared afterward
     */
    public static void saveAllPlayers(boolean removeFromRegistry) {
        for (PlayerStats stats : registry.values()) {
            stats.save(removeFromRegistry);
        }
    }

    public static void saveGlobal() {
        globalSpawns.saveTo(globalConfig, "Spawns");
        globalKills.saveTo(globalConfig, "Kills");
        globalConfig.save();
    }

    /**
     * Similar to creating a new PlayerStats object, except a file isn't created if the player never joined
     * the server
     *
     * @param uuid the uuid of the player
     * @return the player's stats, or null if there are none.
     */
    public static @Nullable PlayerStats getStats(UUID uuid) {
        if (registry.containsKey(uuid)) {
            // if the player is online, get most recent info from registry
            return registry.get(uuid);
        }
        File dataFile = new File(CustomBosses.getInstance().getDataFolder().getPath() + File.separator + "data");
        if (dataFile.exists()) {
            return new PlayerStats(uuid);
        }
        return null;
    }

    /**
     * @see PlayerStats#getStats(UUID)
     */
    public static Map<UUID, PlayerStats> getRegistry() {
        return registry;
    }
}
