package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.boss.impl.BasicHusk;
import me.redplayer_1.custombosses.boss.impl.BasicZombie;
import me.redplayer_1.custombosses.boss.impl.MinerZombie;
import org.bukkit.Bukkit;

import java.util.LinkedList;

public enum BossType {
    BASIC_ZOMBIE(BasicZombie.class),
    BASIC_HUSK(BasicHusk.class),
    MINER_ZOMBIE(MinerZombie.class);

    public static final String[] values;

    static {
        LinkedList<String> strVals = new LinkedList<>();
        for (BossType type : values()) {
            strVals.add(type.name());
        }
        values = strVals.toArray(new String[0]);
    }
    private final Class<? extends Boss> clazz;

    BossType(Class<? extends Boss> clazz) {
        this.clazz = clazz;
    }

    public Boss newInstance() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Boss subclass didn't have a no-argument constructor!");
            throw new RuntimeException(e);
        }
    }
}
