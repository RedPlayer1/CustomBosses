package me.redplayer_1.custombosses;

import me.redplayer_1.custombosses.api.PlayerStats;
import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.command.BossCommand;
import me.redplayer_1.custombosses.config.Config;
import me.redplayer_1.custombosses.events.BossListener;
import me.redplayer_1.custombosses.events.DamageListener;
import me.redplayer_1.custombosses.events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public final class CustomBosses extends JavaPlugin {
    /*
    TODO:
    - PAPI Hook (boss kills/spawns (global & player specific))
    - Hologram Hook (leaderboards)
    - More Abilities
    - More Bosses
    - Spawn Eggs (custom attributes? / difficulty)
    - Custom Enchants
    - Boss Armor & Weapons
    - Boss Difficulty (increases chance of re-roll if no ability selected, more damage, more HP)
    - Boss damage affected by abilities
    - FINISH saving stats to file (see PlayerStats.save())
     */
    private static CustomBosses instance;
    private Config settings;

    @Override
    public void onEnable() {
        instance = this;
        // load settings
        try {
            settings = new Config("settings");
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Something went wrong while loading the config! Make sure it contains valid syntax. \n(" + e.getMessage() + ")");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Listeners
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BossListener(), this);

        // Commands
        Bukkit.getCommandMap().register("custombosses", new BossCommand());
    }

    @Override
    public void onDisable() {
        // Bosses shouldn't persist shutdowns
        getLogger().info("Removing all spawned bosses. . .");

        List<Boss> despawnQueue = Boss.getRegistry().values().stream().toList();
        for (Boss boss : despawnQueue) {
            boss.despawn();
        }

        // save stats
        PlayerStats.saveAllPlayers(true);
        PlayerStats.saveGlobal();
    }

    public static CustomBosses getInstance() {
        return instance;
    }

    public Config getSettings() {
        return settings;
    }
}
