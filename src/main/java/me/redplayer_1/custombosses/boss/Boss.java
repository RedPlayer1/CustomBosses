package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.util.MessageUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Boss {
    private static HashMap<UUID, Boss> registry = new HashMap<>();
    private final DecimalFormat healthFormat = new DecimalFormat("0.00");
    public static final String UUID_METADATA_KEY = "boss_uuid";
    private NPC entity;
    private List<BossAbility> abilities;
    private final UUID uuid;
    private final BossConfig config;

    public Boss(BossConfig config, BossAbility... abilities) {
        uuid = UUID.randomUUID();
        this.config = config;
        this.abilities = Arrays.stream(abilities).toList();
    }

    public abstract Boss copy();

    public void addAbility(BossAbility ability) {
        abilities.add(ability);
    }

    /**
     * Spawns this boss. When overriding, make sure to call
     * the super method
     * @param loc the location to spawn the boss
     */
    public void spawn(Location loc) {
        entity = CitizensAPI.getNPCRegistry().createNPC(EntityType.valueOf(config.getEntityType()), config.getName());
        entity.addTrait(new BossTrait(this));
        entity.setProtected(false);
        entity.spawn(loc);
        entity.getEntity().setInvulnerable(false);
        registerBoss(this);
    }

    /**
     * Despawns the boss. When overriding, make sure to call
     * the super method
     */
    public void despawn() {
        if (entity.isSpawned()) {
            ((LivingEntity) entity.getEntity()).setHealth(0);
            entity.despawn();
        }
        unregisterBoss(this);
    }

    /**
     * Fired when the boss is killed by an entity.
     * @param killer the entity who killed the boss
     */
    public abstract void onKill(@Nullable LivingEntity killer);

    public static void registerBoss(Boss boss) {
        registry.put(boss.entity.getUniqueId(), boss);
    }

    public static void unregisterBoss(Boss boss) {
        registry.remove(boss.entity.getUniqueId());
        boss.entity.destroy();
    }

    public static @Nullable Boss getBoss(UUID uuid) {
        return registry.get(uuid);
    }
    public static HashMap<UUID, Boss> getRegistry() {
        return registry;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BossConfig getConfig() {
        return config;
    }

    public NPC getEntity() {
        return entity;
    }

    public @Nullable Location getLocation() {
        if (entity.isSpawned()) return entity.getStoredLocation();
        return null;
    }

    public String getFormattedName(double health) {
        String result = CustomBosses.getInstance().getSettings().getConfig().getString("Boss.nameFormat");
        return MessageUtils.replacePlaceholders(
                new String[]{"{name}", "{health}"}, new Object[]{config.getName(), healthFormat.format(health)},
                result);
    }
}
