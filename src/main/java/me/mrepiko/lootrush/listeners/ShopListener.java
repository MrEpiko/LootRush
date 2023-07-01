package me.mrepiko.lootrush.listeners;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.SoloPlayer;
import me.mrepiko.lootrush.manager.Team;
import me.mrepiko.lootrush.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopListener implements Listener {

    @EventHandler
    public void inventory(InventoryClickEvent event) {

        if (!event.getInventory().getTitle().equalsIgnoreCase(LootRush.getShopTitle())) return;
        
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        if (event.getCurrentItem().getItemMeta().getLore() == null) return;

        int price = -1;

        for (String w: event.getCurrentItem().getItemMeta().getLore().get(event.getCurrentItem().getItemMeta().getLore().size() - 1).split(" ")) {
            try {
                price = Integer.parseInt(w);
            } catch (NumberFormatException ex) {
                //
            }
        }

        if (price == -1) return;
        if (LootRush.getEventStatus() != EventStatus.ACTIVE) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (LootRush.getAutoEquipArmor()) {

            boolean canPass = true;

            if (player.getInventory().getHelmet() != null)
                if (player.getInventory().getHelmet().getType() == event.getCurrentItem().getType()) canPass = false;

            if (player.getInventory().getChestplate() != null)
                if (player.getInventory().getChestplate().getType() == event.getCurrentItem().getType()) canPass = false;

            if (player.getInventory().getLeggings() != null)
                if (player.getInventory().getLeggings().getType() == event.getCurrentItem().getType()) canPass = false;

            if (player.getInventory().getLeggings() != null)
                if (player.getInventory().getBoots().getType() == event.getCurrentItem().getType()) canPass = false;

            if (!canPass) {
                player.closeInventory();
                if (!LootRush.getMessages().get("shop-duplicated-armor").equalsIgnoreCase(""))
                    player.sendMessage(LootRush.getMessages().get("shop-duplicated-armor"));
                return;
            }

        }

        boolean hasEnough = false;
        int balance;
        if (LootRush.getEventType() == EventType.SOLO) {

            SoloPlayer pl = LootRush.getRoundManager().getSoloPlayer(player);
            if (pl == null) return;

            balance = pl.getCoins();

            if (pl.getCoins() >= price) {

                LootRush.getRoundManager().getPlayers().get(player.getDisplayName()).setCoins(pl.getCoins() - price);
                hasEnough = true;

            }

        } else if (LootRush.getEventType() == EventType.TEAM) {

            Team tm = LootRush.getRoundManager().getTeamOfPlayer(player);
            if (tm == null) return;

            balance = tm.getPlayerCoins(player);

            if (tm.getPlayerCoins(player) >= price) {

                LootRush.getRoundManager().getTeams().get(tm.getColor()).setPlayerCoins(player, tm.getPlayerCoins(player) - price);
                hasEnough = true;

            }

        } else {
            return;
        }

        if (hasEnough) {

            ItemStack item = event.getCurrentItem().clone();
            ItemMeta meta = item.getItemMeta();
            List<String> newLore = new ArrayList<>();
            for (int i = 0; i < item.getItemMeta().getLore().size(); i++) {

                if (i == item.getItemMeta().getLore().size() - 1) continue;

                newLore.add(item.getItemMeta().getLore().get(i));

            }
            meta.setLore(newLore);
            item.setItemMeta(meta);

            if (LootRush.getAutoEquipArmor()) {

                if (item.getType().name().endsWith("_HELMET")) player.getInventory().setHelmet(item);
                else if (item.getType().name().endsWith("_CHESTPLATE")) player.getInventory().setChestplate(item);
                else if (item.getType().name().endsWith("_LEGGINGS")) player.getInventory().setLeggings(item);
                else if (item.getType().name().endsWith("_BOOTS")) player.getInventory().setBoots(item);
                else player.getInventory().addItem(item);

            } else player.getInventory().addItem(item);

            if (!LootRush.getMessages().get("shop-purchase").equalsIgnoreCase(""))
                player.sendMessage(LootRush.getMessages().get("shop-purchase").replace("{item}", ItemUtils.getItemName(item)).replace("{price}", String.valueOf(price)));
            return;

        }

        if (!LootRush.getMessages().get("shop-failure").equalsIgnoreCase(""))
            player.sendMessage(LootRush.getMessages().get("shop-failure").replace("{item}", ItemUtils.getItemName(event.getCurrentItem())).replace("{missing}", String.valueOf(price - balance)));

        if (LootRush.getCloseShopUponFailure()) player.closeInventory();

    }

}
