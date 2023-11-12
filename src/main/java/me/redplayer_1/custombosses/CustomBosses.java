package me.redplayer_1.custombosses;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossTrait;
import me.redplayer_1.custombosses.command.BossCommand;
import me.redplayer_1.custombosses.config.Config;
import me.redplayer_1.custombosses.events.MainListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.LinkedList;
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
    - Boss Persistence (serialize on shutdown - avoid unregistered trait error)
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
        // Citizens Trait
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BossTrait.class));

        // Listeners
        getServer().getPluginManager().registerEvents(new MainListener(), this);

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
    }

    public static CustomBosses getInstance() {
        return instance;
    }

    public Config getSettings() {
        return settings;
    }
}
