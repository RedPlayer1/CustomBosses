package me.redplayer_1.custombosses.abilities.impl;

import me.redplayer_1.custombosses.abilities.CooldownBossAbility;
import me.redplayer_1.custombosses.boss.BossEntity;
import me.redplayer_1.custombosses.util.CachedList;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class StasisAbility extends CooldownBossAbility {
    private static final int DURATION = 4;
    private static final CachedList<Player> cache = new CachedList<>(DURATION, TimeUnit.SECONDS,
            player -> {
                AttributeInstance attr = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                attr.setBaseValue(attr.getBaseValue() - 1);
            });

    public StasisAbility() {
        this(0.8);
    }

    public StasisAbility(double chance) {
        super("<gradient:green:yellow>Stasis</gradient>", false, chance, DURATION * 20 * 2 /* this ability is very op */);
    }

    @Override
    public boolean use(BossEntity bossEntity, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, DURATION * 20, 69, false, true, false));
        AttributeInstance attr = target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        attr.setBaseValue(attr.getBaseValue() + 1); // don't take kb from attacks (no rubberbanding)
        cache.add(target);
        // TODO: add particle (helix/sphere around affected players)
        return true;
    }

    public static class StasisListener implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            if (event.getTo().getY() > event.getFrom().getY() && cache.contains(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().setFreezeTicks(120);
            }

        }

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            cache.remove(event.getPlayer()); // reset effects ability on death (slowness removed by default)
        }
    }
}
