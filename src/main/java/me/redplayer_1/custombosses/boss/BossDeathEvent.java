package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.entity.MobDeathEvent;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BossDeathEvent extends MobDeathEvent {
    private final Boss killed;

    public BossDeathEvent(@NotNull Boss killed, @Nullable Entity killer) {
        super(killed.getMob(), killer);
        this.killed = killed;
    }

    /**
     * @return The Boss that was killed
     */
    public Boss getKilledBoss() {
        return killed;
    }
}
