package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.api.PlayerStats;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.entity.Mob;
import me.redplayer_1.custombosses.util.LocationUtils;
import me.redplayer_1.custombosses.util.MessageUtils;
import me.redplayer_1.custombosses.util.SyntaxParser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The root class for all Bosses. For any subclass in {@link BossType}, it must have a no-argument constructor.
 * The recommended approach is:
 * <pre>{@code
 * public class MyBoss extends Boss {
 *     public MyBoss() {
 *         super(args...);
 *     }
 * }
 * }</pre>
 */
public abstract class Boss {
    private static final Random random = new Random();
    private static final HashMap<UUID, Boss> registry = new HashMap<>();
    private final SyntaxParser msgParser = new SyntaxParser(new String[]{"{player}", "{boss}"});
    private Mob entity;
    private List<BossAbility> abilities;
    private final BossConfig config;

    public Boss(BossConfig config, BossAbility... abilities) {
        this.config = config;
        this.abilities = Arrays.stream(abilities).toList();
    }

    public void addAbility(BossAbility ability) {
        abilities.add(ability);
    }

    /**
     * Spawns this boss. When overriding, make sure to call
     * the super method
     *
     * @param loc the location to spawn the boss
     */
    public final void spawn(Location loc, @Nullable Entity spawner) {
        SpawnBuilder builder = new SpawnBuilder();
        onPreSpawn(loc, builder);
        Bukkit.getScheduler().runTaskLater(CustomBosses.getInstance(), mainTask -> {
            entity = new Mob(config.name(), EntityType.valueOf(config.entityType()), loc, config.health(), true, 4, true);
            registerBoss(this);

            // stat increment
            if (spawner != null) {
                PlayerStats stats = PlayerStats.getRegistry().get(spawner.getUniqueId());
                if (stats != null) {
                    stats.incrementSpawn(config.bossType());
                }
            }
            onSpawn();

            // ability task
            Bukkit.getScheduler().runTaskTimer(CustomBosses.getInstance(), task -> {
                if (entity.isDead()) {
                    task.cancel();
                } else {
                    useAbility();
                }
            }, 5, BossAbility.DEFAULT_USAGE_DELAY);

            // announce spawn
            FileConfiguration settings = CustomBosses.getInstance().getSettings().getConfig();
            if (settings.getBoolean("Boss.broadcastSpawn")) {
                String broadcastMsg;
                if (spawner != null) {
                    broadcastMsg = msgParser.parse(settings.getString("Boss.spawnBroadcastMessagePlayer"), spawner.getName(), config.name());
                } else {
                    broadcastMsg = msgParser.parse(settings.getString("Boss.spawnBroadcastMessageAnonymous"), null, config.name());
                }
                Bukkit.broadcast(MessageUtils.mmsgToComponent(broadcastMsg));
            }
        }, builder.delay);

    }

    /**
     * Despawns the boss. This will destroy the boss and its related data.
     * @see Boss#kill(LivingEntity)
     */
    public final void despawn() {
        if (!entity.isDead()) {
            entity.kill(true);
        }
        unregisterBoss(this);
    }

    /**
     * Kills and automatically {@link Boss#despawn() despawns} the boss.
     * @param killer the entity who killed the boss
     */
    public final void kill(@Nullable LivingEntity killer) {
        // announce death
        FileConfiguration settings = CustomBosses.getInstance().getSettings().getConfig();
        if (settings.getBoolean("Boss.broadcastDeath")) {
            String deathMsg;
            if (killer != null) {
                deathMsg = msgParser.parse(
                        settings.getString("Boss.deathBroadcastMessagePlayer"),
                        killer.getName(),
                        config.name()
                );
            } else {
                deathMsg = msgParser.parse(
                        settings.getString("Boss.deathBroadcastMessageAnonymous"),
                        null,
                        config.name()
                );
            }
            Bukkit.broadcast(MessageUtils.mmsgToComponent(deathMsg));
        }
        // trigger event
        if (killer != null) {
            PlayerStats stats = PlayerStats.getRegistry().get(killer.getUniqueId());
            if (stats != null) {
                stats.incrementKill(config.bossType());
            }
        }
        onKill(entity.getLocation(), killer);
        despawn();
    }

    protected void useAbility() {
        if (abilities.isEmpty()) return;
        for (BossAbility ability : abilities) {
            // calculate chance
            if (random.nextInt(1, 100) <= ability.getChance() * 100) {
                if (ability instanceof CooldownBossAbility cba && !cba.canUse()) {
                    // return if the ability has an unreached cooldown
                    return;
                }
                // give resistance so the boss won't kill itself
                if (!ability.isSingleTarget()) {
                    // use ability on all players in range
                    for (Player p : entity.getLocation().getNearbyPlayers(config.attackRange(), player -> player.getGameMode() == GameMode.SURVIVAL)) {
                        ability.use(this, p);
                        p.sendActionBar(
                                MessageUtils.mmsgToComponent(String.format(BossAbility.USAGE_MESSAGE, config.name(), ability.getName()))
                        );
                    }
                } else {
                    // use ability on closest player (in range)
                    Player player = LocationUtils.getClosestPlayer(entity.getLocation(), true, config.attackRange());
                    if (player == null) return;

                    ability.use(this, player);
                    player.sendActionBar(
                            MessageUtils.mmsgToComponent(String.format(BossAbility.USAGE_MESSAGE, config.name(), ability.getName()))
                    );
                }
                return;
            }
        }
    }

    /**
     * Called before the Boss is spawned. The entity representing the Boss doesn't exist yet.
     *
     * @param spawnLocation the location that the Boss will spawn
     */
    public abstract void onPreSpawn(Location spawnLocation, SpawnBuilder builder);

    /**
     * Called after the Boss is spawned. The entity representing this Boss will not be null.
     */
    public abstract void onSpawn();

    /**
     * Fired when the Boss is killed. The entity corresponding to this Boss will be null.
     * @param location the location of the Boss when it was killed
     * @param killer the entity that killed the Boss
     */
    public abstract void onKill(@NotNull Location location, @Nullable LivingEntity killer);

    public static void registerBoss(Boss boss) {
        registry.put(boss.entity.getUuid(), boss);
    }

    public static void unregisterBoss(Boss boss) {
        registry.remove(boss.entity.getUuid());
    }

    /**
     * @param uuid the UUID of the {@link Mob} that represents this Boss
     */
    public static @Nullable Boss getBoss(UUID uuid) {
        return registry.get(uuid);
    }

    public static HashMap<UUID, Boss> getRegistry() {
        return registry;
    }

    public BossConfig getConfig() {
        return config;
    }

    public final Mob getMob() {
        return entity;
    }

    /**
     * @return the location of the entity that represents the Boss
     */
    public @Nullable Location getLocation() {
        if (entity.isDead()) return null;
        return entity.getLocation();
    }

    public List<BossAbility> getAbilities() {
        return abilities;
    }

    /**
     * @return if the Entity is a Boss
     */
    public static boolean isBoss(Entity entity) {
        return of(entity) != null;
    }

    /**
     * @return if the Mob is a Boss
     */
    public static boolean isBoss(Mob mob) {
        return registry.containsKey(mob.getUuid());
    }

    public static @Nullable Boss of(Entity entity) {
        if (!(entity instanceof LivingEntity)) return null;
        Mob mob = Mob.fromBukkit((LivingEntity) entity);
        if (mob != null && registry.containsKey(mob.getUuid())) {
            return registry.get(mob.getUuid());
        }
        return null;
    }

    public static @Nullable Boss of(Mob mob) {
        return registry.get(mob.getUuid());
    }

    public static class SpawnBuilder {
        private long delay = 0; // the delay in ticks

        public void addDelay(long delay) {
            this.delay += delay;
        }
    }
}

