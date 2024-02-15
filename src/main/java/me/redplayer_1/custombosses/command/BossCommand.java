package me.redplayer_1.custombosses.command;

import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.config.providers.BossConfig;
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
        final String noPermMsg = "<red>No Permission!</red>";
        if (!sender.hasPermission(Permission.COMMAND_BOSS)) {
            sender.sendRichMessage(noPermMsg);
            return true;
        }
        if (args.length < 2) return false;
        if (args[1].equalsIgnoreCase("despawn")) {
            if (!sender.hasPermission(Permission.COMMAND_BOSS_KILL)) {
                sender.sendRichMessage(noPermMsg);
                return true;
            }
            // despawn all bosses of that type
            List<UUID> despawnQueue = new LinkedList<>();

            for (Map.Entry<UUID, BossEntity> i : BossEntity.getRegistry().entrySet()) {
                if (args[0].equals(i.getValue().getConfig().getBossType())) {
                    despawnQueue.add(i.getKey());
                }
            }
            for (UUID i : despawnQueue) {
                BossEntity.getRegistry().remove(i).despawn();
            }
        }
        if (sender instanceof Player player && args[1].equalsIgnoreCase("spawn")) {
            if (!sender.hasPermission(Permission.COMMAND_BOSS_SPAWN)) {
                sender.sendRichMessage(noPermMsg);
                return true;
            }
            // spawn a new boss of that type
            BossConfig config = BossConfig.getType(args[0]);
            if (config == null) return false; // type wasn't registered
            BossEntity bossEntity = new BossEntity(config);
            player.sendMessage(MessageUtils.mmsgToComponent("<gray>Spawning a <red><i>" + bossEntity.getConfig().getDisplayName() + "."));
            bossEntity.spawn(player.getLocation(), player);
        } else if (args[1].equalsIgnoreCase("spawn")) {
            // coords must be specified if used from console (arg 2-5)
            // x, y, z, world
            if (args.length < 6) return false;
            try {
                int[] coords = new int[]{
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4])
                };
                new BossEntity(Objects.requireNonNull(BossConfig.getType(args[0]))).spawn(
                        new Location(Bukkit.getWorld(args[5]), coords[0], coords[1], coords[2]),
                        null
                );
            } catch (NullPointerException | NumberFormatException ignored) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return BossConfig.getRegisteredTypes().stream().toList();
        } else if (args.length == 2) {
            return List.of("spawn", "despawn");
        } else {
            return Collections.emptyList();
        }
    }
}
