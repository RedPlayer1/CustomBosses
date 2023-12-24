package me.redplayer_1.custombosses.command;

import me.redplayer_1.custombosses.boss.Boss;
import me.redplayer_1.custombosses.boss.BossType;
import me.redplayer_1.custombosses.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BossCommand extends Command {

    public BossCommand() {
        super("boss", "the root command for all boss utilities", "/boss <args>", Arrays.asList("boss", "custombosses"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 2) return false;
        if (args[1].equalsIgnoreCase("despawn")) {
            // despawn all bosses of that type
            String targetBoss = BossType.valueOf(args[0].toUpperCase()).name();
            List<Boss> despawnQueue = new LinkedList<>();

            for (Map.Entry<UUID, Boss> i : Boss.getRegistry().entrySet()) {
                if (targetBoss.equalsIgnoreCase(args[0])) {
                    despawnQueue.add(i.getValue());
                }
            }
            for (Boss i : despawnQueue) {
                i.despawn();
            }
        }
        if (sender instanceof Player player && args[1].equalsIgnoreCase("spawn")) {
            // spawn a new boss of that type
            try {
                Boss boss = BossType.valueOf(args[0].toUpperCase()).newInstance();
                player.sendMessage(MessageUtils.mmsgToComponent("<gray>Spawning a <red><i>" + boss.getConfig().name() + "."));
                boss.spawn(player.getLocation(), player);
            } catch (IllegalArgumentException e) {
                player.sendPlainMessage("Invalid Name!");
                return false;
            }
        } else {
            // coords must be specified if used from console (arg 2-5)
            // x, y, z, world
            if (args.length < 6) return false;
            int[] coords = new int[]{
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4])
            };
            if (args[1].equalsIgnoreCase("spawn")) {
                try {
                    BossType.valueOf(args[0].toUpperCase()).newInstance().spawn(
                            new Location(Bukkit.getWorld(args[5]), coords[0], coords[1], coords[2]),
                            null
                    );
                } catch (NullPointerException | NumberFormatException ignored) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return List.of(BossType.values);
        } else if (args.length == 2) {
            return List.of("spawn", "despawn");
        } else {
            return Collections.emptyList();
        }
    }
}
