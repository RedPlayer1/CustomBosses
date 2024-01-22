package me.redplayer_1.custombosses.util;

import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A stores a sequence of commands
 */
public class CommandSequence {
    // guaranteed to have equal lengths
    private final List<String> strings;
    private List<SequenceType> types;

    public CommandSequence() {
        strings = new ArrayList<>();
        types = new ArrayList<>();
    }

    /**
     * Creates a new sequence with the commands (does not support delays)
     * @param commands the commands
     */
    public CommandSequence(Collection<String> commands) {
        this();
        for (String cmd : commands) {
            strings.add(
                    cmd.startsWith("/") ? cmd.replaceFirst("/", "") : cmd
            );
        }
        for (int i = 0; i < commands.size(); i++) {
            types.add(SequenceType.COMMAND);
        }
    }

    /**
     * Runs the sequence (as console)
     */
    public void run() {
        runRecursive(0, 0);
    }

    private void runRecursive(int delay, int startIndex) {
        Bukkit.getScheduler().runTaskLater(CustomBosses.getInstance(), () -> {
            for (int index = startIndex; index < strings.size(); index++) {
                if (types.get(index) == SequenceType.DELAY) {
                    runRecursive(Integer.parseInt(strings.get(index)), index);
                    return;
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), strings.get(index));
                }
            }
        }, delay);
    }

    public void addCommand(String command) {
        strings.add(command);
        types.add(SequenceType.COMMAND);
    }

    public void addDelay(int ticks) {
        strings.add(String.valueOf(ticks));
        types.add(SequenceType.DELAY);
    }

    private enum SequenceType {
        COMMAND,
        DELAY
    }
}
