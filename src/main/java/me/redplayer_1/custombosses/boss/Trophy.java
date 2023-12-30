package me.redplayer_1.custombosses.boss;

import me.redplayer_1.custombosses.CustomBosses;
import me.redplayer_1.custombosses.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Trophy extends ItemStack {
    private static final NamespacedKey KEY = new NamespacedKey(CustomBosses.getInstance(), "trophy");

    public Trophy(Material material, String name, List<String> lore) {
        super(material);
        ItemStack item = ItemUtils.createItem(material, name, lore, true);
        setItemMeta(item.getItemMeta());

        editMeta(meta -> meta.getPersistentDataContainer().set(
                KEY, PersistentDataType.BYTE, (byte) 1
        ));
    }

    public static boolean isTrophy(ItemStack item) {
        return item.getType() != Material.AIR && item.getItemMeta().getPersistentDataContainer().has(KEY);
    }
}
