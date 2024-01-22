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
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BossEntity {
    private static final Random random = new Random();
    private static final HashMap<UUID, BossEntity> registry = new HashMap<>();
    private final SyntaxParser msgParser = new SyntaxParser("{player}", "{boss}");
    private Mob entity;
    private final List<BossAbility> abilities = new ArrayList<>();
    private final BossConfig config;

    public BossEntity(BossConfig config) {
        this.config = config;
        config.getAbilities().forEach(ability -> this.abilities.add(ability.create()));
    }

    /**
     * Spawns this boss. When overriding, make sure to call
     * the super method
     *
     * @param loc the location to spawn the boss
     */
    public final void spawn(Location loc, @Nullable Entity spawner) {
        SpawnBuilder builder = new SpawnBuilder();
        config.doIfBoss(boss -> boss.onPreSpawn(loc, builder));
        Bukkit.getScheduler().runTaskLater(CustomBosses.getInstance(), mainTask -> {
            entity = new Mob(config.getDisplayName(), config.getEntityType(), loc, config.getHealth(), true, config.getAttackRange(), true);
            entity.setDamageScalar(config.getDamageScalar());
            registerBoss(this);

            // stat increment
            if (spawner != null) {
                PlayerStats stats = PlayerStats.getRegistry().get(spawner.getUniqueId());
                if (stats != null) {
                    stats.incrementSpawn(config.getBossType());
                }
            }
            config.doIfBoss(boss -> boss.onSpawn(this));
            // make it harder for players to constantly knock Boss back & avoid damage
            entity.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.6);

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
                    broadcastMsg = msgParser.parse(settings.getString("Boss.spawnBroadcastMessagePlayer"), spawner.getName(), config.getDisplayName());
                } else {
                    broadcastMsg = msgParser.parse(settings.getString("Boss.spawnBroadcastMessageAnonymous"), null, config.getDisplayName());
                }
                Bukkit.broadcast(MessageUtils.mmsgToComponent(broadcastMsg));
            }
        }, builder.delay);

    }

    /**
     * Despawns the boss. This will destroy the boss and its related data.
     * @see BossEntity#kill(LivingEntity)
     */
    public final void despawn() {
        if (!entity.isDead()) {
            entity.kill(true);
        }
        unregisterBoss(this);
    }

    /**
     * Kills and automatically {@link BossEntity#despawn() despawns} the boss.
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
                        config.getDisplayName()
                );
            } else {
                deathMsg = msgParser.parse(
                        settings.getString("Boss.deathBroadcastMessageAnonymous"),
                        null,
                        config.getDisplayName()
                );
            }
            Bukkit.broadcast(MessageUtils.mmsgToComponent(deathMsg));
        }
        // trigger event
        if (killer != null) {
            PlayerStats stats = PlayerStats.getRegistry().get(killer.getUniqueId());
            if (stats != null) {
                stats.incrementKill(config.getBossType());
            }
        }
        config.doIfBoss(boss -> boss.onKill(entity.getLocation(), killer));
        despawn();
    }

    private void useAbility() {
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
                    for (Player p : entity.getLocation().getNearbyPlayers(config.getAttackRange(), player -> player.getGameMode() == GameMode.SURVIVAL)) {
                        ability.use(this, p);
                        p.sendActionBar(
                                MessageUtils.mmsgToComponent(String.format(BossAbility.USAGE_MESSAGE, config.getPlainName(), ability.getName()))
                        );
                    }
                } else {
                    // use ability on closest player (in range)
                    Player player = LocationUtils.getClosestPlayer(entity.getLocation(), true, config.getAttackRange());
                    if (player == null) return;

                    ability.use(this, player);
                    player.sendActionBar(
                            MessageUtils.mmsgToComponent(String.format(BossAbility.USAGE_MESSAGE, config.getPlainName(), ability.getName()))
                    );
                }
                return;
            }
        }
    }

    public static void registerBoss(BossEntity bossEntity) {
        registry.put(bossEntity.entity.getUuid(), bossEntity);
    }

    public static void unregisterBoss(BossEntity bossEntity) {
        registry.remove(bossEntity.entity.getUuid());
    }

    /**
     * @param uuid the UUID of the {@link Mob} that represents this Boss
     */
    public static @Nullable BossEntity getBoss(UUID uuid) {
        return registry.get(uuid);
    }

    public static HashMap<UUID, BossEntity> getRegistry() {
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

    public static @Nullable BossEntity of(Entity entity) {
        if (!(entity instanceof LivingEntity)) return null;
        Mob mob = Mob.fromBukkit((LivingEntity) entity);
        if (mob != null && registry.containsKey(mob.getUuid())) {
            return registry.get(mob.getUuid());
        }
        return null;
    }

    public static @Nullable BossEntity of(Mob mob) {
        return registry.get(mob.getUuid());
    }

    public static class SpawnBuilder {
        private long delay = 0; // the delay in ticks

        public void addDelay(long delay) {
            this.delay += delay;
        }
    }
}

