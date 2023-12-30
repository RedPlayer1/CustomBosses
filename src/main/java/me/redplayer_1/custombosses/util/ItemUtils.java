package me.redplayer_1.custombosses.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

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
     * @param unbreakable if the item shouldn't lose durability
     * @return the created item
     */
    public static ItemStack createItem(Material material, @Nullable String name, @Nullable List<String> lore, boolean unbreakable) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.displayName(MessageUtils.mmsgToComponent(name));
        if (lore != null) {
            List<Component> newLore = new LinkedList<>();
            for (String str: lore) {
                newLore.add(MessageUtils.mmsgToComponent(str));
            }
            meta.lore(newLore);
        }
        meta.setUnbreakable(unbreakable);

        item.setItemMeta(meta);
        return item;
    }

    public record Enchant(Enchantment enchantment, int level) {}

    /**
     * Creates an item with enchantments
     * @see ItemUtils#createItem(Material, String, List, boolean)
     * @see ItemStack#addEnchantment(Enchantment, int)
     * @param onlySafe only apply safe enchantments
     * @param enchantments enchantments to apply
     * @return the new ItemStack
     */
    public static ItemStack createItem(Material material, String name, List<String> lore, boolean unbreakable, boolean onlySafe, Enchant... enchantments) {
        ItemStack item = createItem(material, name, lore, unbreakable);
        for (Enchant enchant : enchantments) {
            if (onlySafe)
                item.addEnchantment(enchant.enchantment, enchant.level);
            else
                item.addUnsafeEnchantment(enchant.enchantment, enchant.level);
        }
        return item;
    }

    /**
     * Applies the given equipment to the entity
     */
    public static void applyEquipment(LivingEntity entity, float dropChance, @Nullable ItemStack helmet, @Nullable ItemStack chestplate, @Nullable ItemStack leggings, @Nullable ItemStack boots, @Nullable ItemStack mainHand, @Nullable ItemStack offHand) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        equipment.setHelmet(helmet);
        equipment.setHelmetDropChance(dropChance);
        equipment.setChestplate(chestplate);
        equipment.setChestplateDropChance(dropChance);
        equipment.setLeggings(leggings);
        equipment.setLeggingsDropChance(dropChance);
        equipment.setBoots(boots);
        equipment.setBootsDropChance(dropChance);
        equipment.setItemInMainHand(mainHand);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(offHand);
        equipment.setItemInOffHandDropChance(dropChance);
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
