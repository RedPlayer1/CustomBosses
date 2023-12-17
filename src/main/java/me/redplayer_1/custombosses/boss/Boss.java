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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public abstract class Boss {
    private static final Random random = new Random();
    private static final HashMap<UUID, Boss> registry = new HashMap<>();
    private final DecimalFormat healthFormat = new DecimalFormat("0.00");
    private final SyntaxParser msgParser = new SyntaxParser(new String[]{"{player}", "{boss}"});
    public static final String UUID_METADATA_KEY = "boss_uuid";
    private Mob entity;
    private List<BossAbility> abilities;
    private final BossConfig config;

    public Boss(BossConfig config, BossAbility... abilities) {
        //uuid = UUID.randomUUID();
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
        onPreSpawn(loc);
        entity = new Mob(config.getName(), EntityType.valueOf(config.getEntityType()), loc, config.getHealth(), 4, true);
        registerBoss(this);

        if (spawner != null) {
            PlayerStats stats = PlayerStats.getRegistry().get(spawner.getUniqueId());
            if (stats != null) {
                stats.incrementSpawn(BossFactory.typeOf(this));
            }
        }
        onSpawn();

        // announce spawn
        FileConfiguration settings = CustomBosses.getInstance().getSettings().getConfig();
        if (settings.getBoolean("Boss.broadcastSpawn")) {
            String broadcastMsg;
            if (spawner != null) {
                broadcastMsg = msgParser.parse(settings.getString("Boss.spawnBroadcastMessagePlayer"), spawner.getName(), config.getName());
            } else {
                broadcastMsg = msgParser.parse(settings.getString("Boss.spawnBroadcastMessageAnonymous"), null, config.getName());
            }
            Bukkit.broadcast(MessageUtils.miniMessageToComponent(broadcastMsg));
        }
    }

    /**
     * Despawns the boss. This will instantly destroy the boss and its related data.
     */
    public final void despawn() {
        if (!entity.isDead()) {
            entity.kill();
        }
        unregisterBoss(this);
    }

    /**
     * Kills and automatically despawn the boss. Called right before the boss is killed
     *
     * @param killer the entity who killed the boss
     */
    public final void kill(@Nullable LivingEntity killer) {
        // announce death
        FileConfiguration settings = CustomBosses.getInstance().getSettings().getConfig();
        if (settings.getBoolean("Boss.broadcastDeath")) {
            String deathMsg;
            if (killer != null) {
                deathMsg = msgParser.parse(settings.getString("Boss.deathBroadcastMessagePlayer"), killer.getName(), config.getName());
            } else {
                deathMsg = msgParser.parse(settings.getString("Boss.deathBroadcastMessageAnonymous"), null, config.getName());
            }
            Bukkit.broadcast(MessageUtils.miniMessageToComponent(deathMsg));
        }
        // trigger event
        if (killer != null) {
            PlayerStats stats = PlayerStats.getRegistry().get(killer.getUniqueId());
            if (stats != null) {
                stats.incrementKill(BossFactory.typeOf(this));
            }
        }
        onKill(killer);
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
                ((LivingEntity) entity.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2, 255, false, false));
                if (!ability.isSingleTarget()) {
                    // use ability on all players in range
                    for (Player p : entity.getLocation().getNearbyPlayers(config.getAttackRange(), player -> player.getGameMode() == GameMode.SURVIVAL)) {
                        ability.use(this, p);
                        p.sendActionBar(
                                MessageUtils.miniMessageToComponent(String.format(BossAbility.USAGE_MESSAGE, config.getName(), ability.getName()))
                        );
                    }
                } else {
                    // use ability on closest player (in range)
                    Player player = LocationUtils.getClosestPlayer(entity.getLocation(), true, config.getAttackRange());
                    if (player == null) return;

                    ability.use(this, player);
                    player.sendActionBar(
                            MessageUtils.miniMessageToComponent(String.format(BossAbility.USAGE_MESSAGE, config.getName(), ability.getName()))
                    );
                }
                return;
            }
        }
    }

    public abstract void onPreSpawn(Location spawnLocation);

    public abstract void onSpawn();

    /**
     * Fired when the boss is killed by an entity.
     *
     * @param killer the entity who killed the boss
     */
    public abstract void onKill(@Nullable LivingEntity killer);

    public static void registerBoss(Boss boss) {
        registry.put(boss.entity.getUuid(), boss);
    }

    public static void unregisterBoss(Boss boss) {
        registry.remove(boss.entity.getUuid());
    }

    public static @Nullable Boss getBoss(UUID uuid) {
        return registry.get(uuid);
    }

    public static HashMap<UUID, Boss> getRegistry() {
        return registry;
    }

    public BossConfig getConfig() {
        return config;
    }

    public Mob getMob() {
        return entity;
    }

    public @Nullable Location getLocation() {
        if (entity.isDead()) return null;
        return entity.getLocation();
    }

    public List<BossAbility> getAbilities() {
        return abilities;
    }

    public String getFormattedName(double health) {
        String result = CustomBosses.getInstance().getSettings().getConfig().getString("Boss.nameFormat");
        return MessageUtils.replacePlaceholders(
                new String[]{"{name}", "{health}"}, new Object[]{config.getName(), healthFormat.format(health)},
                result);
    }

    public static boolean isBoss(Entity entity) {
        return of(entity) != null;
    }

    public static @Nullable Boss of(Entity entity) {
        if (!(entity instanceof LivingEntity)) return null;
        Mob mob = Mob.fromBukkit((LivingEntity) entity);
        if (mob != null && registry.containsKey(mob.getUuid())) {
            return registry.get(mob.getUuid());
        }
        return null;
    }
}
