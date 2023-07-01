package me.mrepiko.lootrush.config;

import me.mrepiko.lootrush.LootRush;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class ConfigItem {

    private final String name;
    private final List<String> rawLore;
    private List<String> lore = new ArrayList<>();
    private final byte id;
    private Material material;
    private final int amount;
    private final List<String> enchants;
    private final Color color;
    private final String colorStr;

    private final ItemStack item;
    private final ItemMeta meta;
    private LeatherArmorMeta leatherMeta = null;

    public ConfigItem(ConfigurationSection config, String query) {

        this.name = config.getString(query + ".name");
        this.material = Material.valueOf(config.getString(query + ".material"));
        this.id = (byte) config.getInt(query + ".id");
        this.rawLore = config.getStringList(query + ".lore");
        this.amount = config.getInt(query + ".amount");
        this.enchants = config.getStringList(query + ".enchants");
        this.colorStr = config.getString(query + ".color");

        if (this.colorStr != null) {
            this.color = Color.fromRGB(Integer.parseInt(colorStr.split(",")[0]),
                    Integer.parseInt(colorStr.split(",")[1]),
                    Integer.parseInt(colorStr.split(",")[2]));
        } else {
            this.color = null;
        }

        if (this.material == null) {
            this.material = Material.STONE;
        }

        this.item = new ItemStack(this.material, this.amount, this.id);

        this.meta = this.item.getItemMeta();

        if (name != null) {
            this.meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
        }

        if (this.rawLore.size() > 0) {

            this.rawLore.forEach(l -> this.lore.add(ChatColor.translateAlternateColorCodes('&', l)));
            this.meta.setLore(this.lore);

        }

        if (this.enchants.size() > 0) {

            for (String e: this.enchants) {

                this.meta.addEnchant(Enchantment.getByName(e.split(",")[0]), Integer.parseInt(e.split(",")[1]), true);

            }

        }

        this.item.setItemMeta(this.meta);

        if (this.item.getItemMeta() instanceof LeatherArmorMeta) {
            this.leatherMeta = (LeatherArmorMeta) this.item.getItemMeta();
            this.leatherMeta.setColor(this.color);
            this.item.setItemMeta(this.leatherMeta);
        }

        if (LootRush.getUnbreakableItems()) {

            for (String s: new String[]{"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "SWORD", "PICKAXE", "AXE", "SPADE", "HOE", "BOW"}) {

                if (this.item.getType().name().endsWith(s)) this.item.getItemMeta().spigot().setUnbreakable(true);
            }

        }

    }

    public ItemStack getItemStack() {
        return this.item;
    }

}
