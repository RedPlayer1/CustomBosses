package me.redplayer_1.custombosses.api;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.boss.BossType;
import me.redplayer_1.custombosses.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStats {
    private static final File dataDir = new File(CustomBosses.getInstance().getDataFolder().getPath() + File.separator + "data");
    private static HashMap<UUID, PlayerStats> registry = new HashMap<>();
    private static Map<BossType, Integer> globalKills = getDefaultDataTable();
    private static Map<BossType, Integer> globalSpawns = getDefaultDataTable();

    private Config data;
    private UUID uuid;
    private Map<BossType, Integer> bossKills = getDefaultDataTable();
    private Map<BossType, Integer> bossSpawns = getDefaultDataTable();

    static {
        // ensure existence of data directory
        dataDir.mkdirs();
        // get global info from file
        try {
            Config globalConf = new Config("GlobalStats");
            // load kills
            if (globalConf.getConfig().getConfigurationSection("Kills") != null) {
                Map<String, Object> kills = globalConf.getConfig().getConfigurationSection("Kills").getValues(false);
                for (Map.Entry<String, Object> entry : kills.entrySet()) {
                    globalKills.put(BossType.valueOf(entry.getKey()), (Integer) entry.getValue());
                }
            }
            // load spawns
            if (globalConf.getConfig().getConfigurationSection("Spawns") != null) {
                Map<String, Object> spawns = globalConf.getConfig().getConfigurationSection("Spawns").getValues(false);
                for (Map.Entry<String, Object> entry : spawns.entrySet()) {
                    globalSpawns.put(BossType.valueOf(entry.getKey()), (Integer) entry.getValue());
                }
            }

            globalConf.save();
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Error whilst saving global data!");
        }
    }

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        try {
            data = new Config(dataDir.getPath() + File.separator + uuid);

            // load spawns
            ConfigurationSection section = data.getConfig().getConfigurationSection("Spawns");
            if (section != null) {
                Map<String, Object> spawns = section.getValues(false);
                for (Map.Entry<String, Object> entry : spawns.entrySet()) {
                    bossSpawns.put(BossType.valueOf(entry.getKey()), (Integer) entry.getValue());
                }
            }
            // load kills
            section = data.getConfig().getConfigurationSection("Kills");
            if (section != null) {
                Map<String, Object> kills = section.getValues(false);
                for (Map.Entry<String, Object> entry : kills.entrySet()) {
                    bossKills.put(BossType.valueOf(entry.getKey()), (Integer) entry.getValue());
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("IOException whilst loading data for player " + uuid + "!");
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Invalid syntax found in " + uuid + ".yml!");
        }

        registry.put(uuid, this);
    }

    /**
     * Increments the spawn counter for this type
     * @param type the type of boss spawned
     */
    public void incrementSpawn(BossType type) {
        bossSpawns.put(type, bossSpawns.get(type) + 1);
        globalSpawns.put(type, globalSpawns.get(type) + 1);
    }

    /**
     * Increments the kill counter for this type
     * @param type the type of boss killed
     */
    public void incrementKill(BossType type) {
        bossKills.put(type, bossKills.get(type) + 1);
        globalKills.put(type, globalKills.get(type) + 1);
    }

    /**
     * Attempt to save stats to disk
     * @param remove whether this instance should be removed from the registry (only use on player logouts)
     */
    public void save(boolean remove) {
        // TODO save hashmaps
        HashMap<String, String> strMap = new HashMap<>();
        for (Map.Entry<BossType, Integer> entry : bossSpawns.entrySet()) {
            strMap.put(entry.getKey().name(), entry.getValue().toString());
        }
        data.getConfig().createSection("Spawns", strMap);
        strMap.clear();

        for (Map.Entry<BossType, Integer> entry : bossKills.entrySet()) {
            strMap.put(entry.getKey().name(), entry.getValue().toString());
        }
        data.getConfig().createSection("Kills", strMap);
        data.save();

        if (remove) {
            registry.remove(uuid);
        }
    }

    /**
     * Saves all the player stats in the registry
     * @param removeFromRegistry whether the registry should be cleared afterward
     */
    public static void saveAllPlayers(boolean removeFromRegistry) {
        for (PlayerStats stats : registry.values()) {
            stats.save(false);
        }
        if (removeFromRegistry) {
            registry.clear();
        }
    }

    public static void saveGlobal() {
        try {
            Config globalConf = new Config("GlobalStats");

            globalConf.getConfig().createSection("Kills", globalKills);
            globalConf.getConfig().createSection("Spawns", globalSpawns);

            globalConf.save();
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Error whilst saving global data!");
        }
    }

    /**
     * Similar to creating a new PlayerStats object, except a file isn't created if the player never joined
     * the server
     * @param uuid the uuid of the player
     * @return the player's stats, or null if there are none.
     */
    public static @Nullable PlayerStats getStats(UUID uuid) {
        if (registry.containsKey(uuid)) {
            // if the player is online, get most recent info from registry
            return registry.get(uuid);
        }
        File dataFile = new File(dataDir.getPath() + File.separator + uuid);
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

    /**
     * @return a map with all possible keys having the value 0
     */
    private static Map<BossType, Integer> getDefaultDataTable() {
        Map<BossType, Integer> result = new HashMap<>();
        for (BossType type : BossType.values()) {
            result.put(type, 0);
        }
        return result;
    }
}
