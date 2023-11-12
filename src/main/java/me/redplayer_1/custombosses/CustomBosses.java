package me.redplayer_1.custombosses;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.command.BossCommand;
import me.redplayer_1.custombosses.config.Config;
import me.redplayer_1.custombosses.events.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class CustomBosses extends JavaPlugin {
    /*
    TODO:
    - PAPI Hook (boss kills/spawns (global & player specific))
    - Hologram Hook (leaderboards)
    - More Abilities
    - More Bosses
    - Spawn Eggs (custom attributes)
    - Custom Enchants
    - Boss Armor & Weapons
    - Boss Difficulty (increases chance of re-roll if no ability selected, more damage, more HP)
    - Boss Persistence (serialize on shutdown - avoid unregistered trait error)
     */
    private static CustomBosses instance;
    private Config settings;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        try {
            settings = new Config("settings");
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Something went wrong while loading the config! Make sure it contains valid syntax. \n(" + e.getMessage() + ")");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Listeners
        getServer().getPluginManager().registerEvents(new MainListener(), this);

        // Commands
        Bukkit.getCommandMap().register("custombosses", new BossCommand());
    }

    @Override
    public void onDisable() {
        // Bosses shouldn't persist shutdowns
        getLogger().info("Removing all spawned bosses. . .");
        for (Boss boss : Boss.getRegistry().values()) {
            boss.despawn();
        }
    }

    public static CustomBosses getInstance() {
        return instance;
    }

    public Config getSettings() {
        return settings;
    }
}
