package me.redplayer_1.custombosses.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a {@link Mob} is killed.
 */
public class MobDeathEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private final Mob killed;
    private final Entity killer;

    /**
     * @param killed the killed Mob
     * @param killer the entity that killed it
     */
    public MobDeathEvent(@NotNull Mob killed, @Nullable Entity killer) {
        this.killed = killed;
        this.killer = killer;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * @return The Mob that was killed
     */
    public Mob getKilledMob() {
        return killed;
    }

    /**
     * @return The Entity that killed the Mob
     */
    public Entity getKiller() {
        return killer;
    }
}
