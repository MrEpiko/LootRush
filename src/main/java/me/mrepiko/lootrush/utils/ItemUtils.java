package me.mrepiko.lootrush.utils;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static String getItemName(ItemStack item) {

        if (item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }

        return item.getType().toString();

    }

}
