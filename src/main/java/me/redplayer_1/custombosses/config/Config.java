package me.redplayer_1.custombosses.config;

import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {
    private final File pluginDir;
    private final FileConfiguration fileConfig;
    private final File file;

    /**
     * Attempts to load the specified file. If it doesn't exist, the file is created.
     * However, if a file exists in resources, that is copied to the directory.
     *
     * @param fileName the name of the file (without the postfix)
     */
    public Config(String fileName) throws IOException, InvalidConfigurationException {
        pluginDir = CustomBosses.getInstance().getDataFolder();
        file = new File(pluginDir.getPath() + "/" + fileName + ".yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(fileName + ".yml");
            if (inputStream != null) {
                Files.copy(inputStream, file.toPath());
            } else {
                file.createNewFile();
            }

        }
        fileConfig = new YamlConfiguration();
        fileConfig.load(file);
    }

    /**
     * @return The {@link FileConfiguration} for this Config
     */
    public FileConfiguration getConfig() {
        return fileConfig;
    }

    /**
     * Attempts to save the FileConfiguration to disk
     *
     * @return whether the file was saved successfully
     */
    public boolean save() {
        try {
            fileConfig.save(file);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error whilst saving '" + file.getName() + "' to disk!");
            return false;
        }
    }
}
