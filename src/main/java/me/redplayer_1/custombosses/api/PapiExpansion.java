package me.redplayer_1.custombosses.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getAuthor() {
        return String.join("", CustomBosses.getInstance().getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "custombosses";
    }

    @Override
    public @NotNull String getVersion() {
        return CustomBosses.getInstance().getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // don't unload expansion on reload (required)
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
    /* 
     <bossname> = BossConfig.getBossType() (not display name)

     Needs player:
     %spawns_<bossname>%
     %kills_<bossname>%

     Doesn't need player:
     %global_spawns_<bossname>%
     %global_kills_<bossname>%
     */
        String[] args = params.split("_");

        if (player != null) {
            PlayerStats stats = PlayerStats.getStats(player.getUniqueId());
            if (stats == null) return null;
            StringBuilder bossName = new StringBuilder(); // getBossType() contains underscores
            for (int i = 1; i < args.length; i++) bossName.append(args[i]);

            if (args[0].equalsIgnoreCase("spawns")) {
                return String.valueOf(stats.getSpawns(bossName.toString()));
            } else if (args[0].equalsIgnoreCase("kills")) {
                return String.valueOf(stats.getKills(bossName.toString()));
            }
        } else if (args[0].equalsIgnoreCase("global")) {
            StringBuilder bossName = new StringBuilder();
            for (int i = 2; i < args.length; i++) bossName.append(args[i]);

            if (args[1].equalsIgnoreCase("spawns")) {
                return String.valueOf(PlayerStats.getGlobalSpawns(bossName.toString()));
            } else if (args[1].equalsIgnoreCase("kills")) {
                return String.valueOf(PlayerStats.getGlobalKills(bossName.toString()));
            }
        }
        return null;
    }
}
