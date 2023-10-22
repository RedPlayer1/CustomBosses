package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.BossAbility;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.config.providers.BossConfig;
import me.redplayer_1.custombosses.util.LocationUtils;
import me.redplayer_1.custombosses.util.MessageUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
     *
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

    protected void useAbility() {
        if (abilities.isEmpty()) return;
        for (BossAbility ability : abilities) {
            // calculate chance
            if (random.nextInt(1, 100) <= ability.getChance()*100) {
                if (ability instanceof CooldownBossAbility cba && !cba.canUse()) {
                    // return if the ability has an unreached cooldown
                    return;
                }
                // give resistance so the boss won't kill itself
                ((LivingEntity) entity.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2, 255, false, false));
                if (!ability.isSingleTarget()) {
                    // use ability on all players in range
                    for (Player p : entity.getStoredLocation().getNearbyPlayers(config.getAttackRange(), player -> player.getGameMode() == GameMode.SURVIVAL)) {
                        ability.use(this, p);
                        p.sendActionBar(
                                MessageUtils.miniMessageToComponent(String.format(BossAbility.USAGE_MESSAGE, config.getName(), ability.getName()))
                        );
                    }
                } else {
                    // use ability on closest player (in range)
                    Player player = LocationUtils.getClosestPlayer(entity.getStoredLocation(), true, config.getAttackRange());
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
        return entity.hasMetadata("NPC") && entity.hasMetadata(UUID_METADATA_KEY);
    }
}
