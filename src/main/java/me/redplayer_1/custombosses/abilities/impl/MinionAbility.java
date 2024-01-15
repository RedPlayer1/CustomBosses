package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.BossDeathEvent;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class MinionAbility extends CooldownBossAbility {
    private static final int AMOUNT = 5;
    private static final NamespacedKey KEY = new NamespacedKey(CustomBosses.getInstance(), "minion_owner_id");
    private static HashMap<UUID, HashMap<Integer, LivingEntity>> minions = new HashMap<>();

    public MinionAbility() {
        this(0.4);
    }

    public MinionAbility(double chance) {
        super("<gradient:#73ff00:#83ff6e>Minion</gradient>", true, chance, 120);
    }

    @Override
    public boolean use(BossEntity bossEntity, Player target) {
        Location loc = bossEntity.getLocation();

        if (loc == null) return false;
        HashMap<Integer, LivingEntity> localMinions = minions.get(bossEntity.getMob().getUuid());
        if (localMinions == null) localMinions = new HashMap<>();

        for (int index = 0; index < AMOUNT; index++) {
            if (!localMinions.containsKey(index)) {
                LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
                // give the minions fire resistance, so they don't burn during the day
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1, false, false));
                entity.customName(MessageUtils.mmsgToComponent(getName()));
                entity.setCustomNameVisible(true);
                entity.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, bossEntity.getMob().getUuid().toString() + ":" + index);
                localMinions.put(index, entity);
            }
        }

        minions.put(bossEntity.getMob().getUuid(), localMinions);
        return true;
    }

    public static class MinionListener implements Listener {
        @EventHandler
        public void onMinionDeath(EntityDeathEvent event) {
            String uuid = event.getEntity().getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
            if (uuid != null) {
                String[] data = uuid.split(":");
                minions.get(UUID.fromString(data[0])).remove(Integer.parseInt(data[1]));
            }
        }

        @EventHandler
        public void onBossDeath(BossDeathEvent event) {
            HashMap<Integer, LivingEntity> targetMinions = minions.get(event.getKilled().getMob().getUuid());
            if (targetMinions != null)
                targetMinions.forEach((index, entity) -> entity.remove());
        }
    }
}
