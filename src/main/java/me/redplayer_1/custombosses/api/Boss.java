package me.redplayer_1.custombosses.api;

import me.redplayer_1.custombosses.boss.BossEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Boss {
    /**
     * Called before the Boss is spawned. The entity representing the Boss doesn't exist yet.
     *
     * @param spawnLocation the location that the Boss will spawn
     */
    void onPreSpawn(Location spawnLocation, BossEntity.SpawnBuilder builder);

    /**
     * Called after the Boss is spawned. The entity representing this Boss will not be null.
     */
    void onSpawn(BossEntity boss);

    /**
     * Fired when the Boss is killed. The entity corresponding to this Boss will be null.
     *
     * @param location the location of the Boss when it was killed
     * @param killer   the entity that killed the Boss
     */
    void onKill(@NotNull Location location, @Nullable LivingEntity killer);
}
