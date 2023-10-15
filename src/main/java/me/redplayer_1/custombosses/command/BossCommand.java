package me.redplayer_1.custombosses.command;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.Bosses;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BossCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        try {
            if (args[1].equalsIgnoreCase("despawn")) {
                // despawn all bosses of that type
                String targetBoss = Bosses.valueOf(args[0].toUpperCase()).name();
                List<Boss> despawnQueue = new LinkedList<>();

                for (Map.Entry<UUID, Boss> i : Boss.getRegistry().entrySet()) {
                    if (targetBoss.equalsIgnoreCase(args[0])) {
                        despawnQueue.add(i.getValue());
                    }
                }
                for (Boss i : despawnQueue) {
                    i.despawn();
                }
            } else if (args[1].equalsIgnoreCase("spawn")) {
                // spawn a new boss of that type
                Boss boss = Bosses.valueOf(args[0].toUpperCase()).get().copy();
                if (boss == null) {
                    player.sendPlainMessage("Invalid Name!");
                    return true;
                }
                player.sendPlainMessage("Spawning a " + boss.getConfig().getName() + ".");
                boss.spawn(player.getLocation());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            player.sendPlainMessage("Invalid Arguments!");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return List.of(Bosses.names());
        } else if (args.length == 2) {
            return List.of("spawn", "despawn");
        } else {
            return Collections.emptyList();
        }
    }
}
