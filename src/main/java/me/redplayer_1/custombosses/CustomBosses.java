package me.redplayer_1.custombosses;

import me.redplayer_1.custombosses.boss.BossTrait;
import me.redplayer_1.custombosses.command.BossCommand;
import me.redplayer_1.custombosses.config.Config;
import me.redplayer_1.custombosses.events.PlayerListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class CustomBosses extends JavaPlugin {
    private static CustomBosses instance;
    private Config settings;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        try {
            settings = new Config("settings");
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Something went wrong while loading the config! Make sure it contains valid syntax.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // Commands
        getCommand("boss").setExecutor(new BossCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CustomBosses getInstance() {
        return instance;
    }

    public Config getSettings() {
        return settings;
    }
}
