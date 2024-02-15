package me.redplayer_1.custombosses.util;

import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A stores a sequence of commands
 */
public class CommandSequence {
    // guaranteed to have equal lengths
    private final List<String> strings;
    private final List<SequenceType> types;

    public CommandSequence() {
        strings = new ArrayList<>();
        types = new ArrayList<>();
    }

    /**
     * Creates a new sequence with the commands (does not support delays)
     *
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
     *
     * @return the amount of ticks the sequence will take to complete
     */
    public int run() {
        runRecursive(0, 0, null);
        return getTotalDelay();
    }

    /**
     * Runs the sequence (as console)
     *
     * @param parser Parser to be run on every command. Must have args & values already set.
     * @return the amount of ticks the sequence will take to complete
     */
    public int run(SyntaxParser parser) {
        runRecursive(0, 0, parser);
        return getTotalDelay();
    }

    private void runRecursive(int delay, int startIndex, @Nullable SyntaxParser cmdParser) {
        Bukkit.getScheduler().runTaskLater(CustomBosses.getInstance(), () -> {
            for (int index = startIndex; index < strings.size(); index++) {
                if (types.get(index) == SequenceType.DELAY) {
                    runRecursive(Integer.parseInt(strings.get(index)), index, cmdParser);
                    return;
                } else {
                    String cmd = strings.get(index);
                    if (cmdParser != null) {
                        cmd = cmdParser.parse(cmd);
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            }
        }, delay);
    }

    public int getTotalDelay() {
        int total = 0;
        int i = 0;
        for (SequenceType type : types) {
            if (type == SequenceType.DELAY) {
                total += Integer.parseInt(strings.get(i));
            }
            i++;
        }
        return total;
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
