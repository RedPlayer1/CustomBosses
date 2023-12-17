package me.redplayer_1.custombosses.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ConfigMap<K, V> {
    private HashMap<K, V> map = new HashMap<>();
    final Function<K, String> kToString;
    final Function<V, String> vToString;
    final Function<String, K> stringToK;
    final Function<String, V> stringToV;

    public ConfigMap(Function<K, String> kToString, Function<V, String> vToString, Function<String, K> stringToK, Function<String, V> stringToV) {
        this.kToString = kToString;
        this.vToString = vToString;
        this.stringToK = stringToK;
        this.stringToV = stringToV;
    }

    public ConfigMap(Function<K, String> kToString, Function<V, String> vToString, Function<String, K> stringToK, Function<String, V> stringToV, HashMap<K, V> origin) {
        this(kToString, vToString, stringToK, stringToV);
        map = origin;
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public @Nullable V get(K key) {
        return map.get(key);
    }

    /**
     * Saves this map to the config. This will not save changes to disk.
     *
     * @param config The config to save to
     * @param path   The root path of the map in the config
     */
    public void saveTo(Config config, String path) {
        ConfigurationSection section = config.getConfig().createSection(path);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            section.set(kToString.apply(entry.getKey()), vToString.apply(entry.getValue()));
        }
    }

    /**
     * Load a map from the config.
     *
     * @param config The config to load the map from
     * @param path   The root path of the map in the config
     */
    public void loadFrom(Config config, String path) {
        loadFrom(config, path, null);
    }

    /**
     * Attempts to load the map from the config. If not found, it is set to the default value.
     *
     * @param config the config to load from
     * @param path   the root path of the map in the config
     * @param def    the default value
     */
    public void loadFrom(Config config, String path, @Nullable HashMap<K, V> def) {
        ConfigurationSection section = config.getConfig().getConfigurationSection(path);
        if (section != null) {
            for (String i : section.getKeys(false)) {
                map.put(stringToK.apply(i), stringToV.apply(section.getString(i, "")));
            }
        } else if (def != null) {
            map = def;
        }
    }
}
