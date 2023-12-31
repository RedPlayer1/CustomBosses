package me.redplayer_1.custombosses.boss;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a {@link Boss} is killed.
 */
public class BossDeathEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private final Boss killed;
    private final Entity killer;

    /**
     * @param killed the killed Boss
     * @param killer the entity that killed the Boss
     */
    public BossDeathEvent(@NotNull Boss killed, @Nullable Entity killer) {
        this.killed = killed;
        this.killer = killer;
    }

    /**
     * @return The Boss that was killed
     */
    public Boss getKilled() {
        return killed;
    }

    /**
     * @return The Entity that killed the Mob
     */
    public Entity getKiller() {
        return killer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
