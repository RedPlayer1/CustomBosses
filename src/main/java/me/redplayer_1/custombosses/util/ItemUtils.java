package me.redplayer_1.custombosses.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class ItemUtils {
    /**
     * @see ItemStack#ItemStack(Material)  ItemStack
     * @see ItemMeta#displayName(Component)
     * @see ItemMeta#lore(List)
     * @param material item material
     * @param name the name of the item (supports MiniMessage)
     * @param lore the lore of the item (supports MiniMessage)
     * @return the created item
     */
    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtils.mmsgToComponent(name));
        List<Component> newLore = new LinkedList<>();
        for (String str: lore) {
            newLore.add(MessageUtils.mmsgToComponent(str));
        }
        meta.lore(newLore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Add items to target's inventory (if it exists) and drop those that don't fit.
     * @param target entity to give items to
     * @param location the location to drop the items (if any)
     * @param items the items to give
     */
    public static void giveOrDrop(LivingEntity target, Location location, ItemStack... items) {
        if (target instanceof Player p) {
            for (ItemStack item : p.getInventory().addItem(items).values()) {
                target.getWorld().dropItemNaturally(location, item);
            }
        } else {
            for (ItemStack item : items) {
                location.getWorld().dropItemNaturally(location, item);
            }
        }
    }
}
