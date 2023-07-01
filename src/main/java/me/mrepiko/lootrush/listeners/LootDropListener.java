package me.mrepiko.lootrush.listeners;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.lootdrop.LootDropItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootDropListener implements Listener {

    @EventHandler
    public void blockclick(PlayerInteractEvent event) {

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) return;

        if (LootRush.getEventStatus() != EventStatus.ACTIVE) return;

        if (LootRush.getEventType() == EventType.NONE) return;

        if (
                (!event.getClickedBlock().getType().equals(LootRush.getLootDrop().getMaterial()))
                || (event.getClickedBlock().getLocation().getBlockX() != LootRush.getLootDrop().getCurrentLocation().getBlockX())
                || (event.getClickedBlock().getLocation().getBlockY() != LootRush.getLootDrop().getCurrentLocation().getBlockY())
                || (event.getClickedBlock().getLocation().getBlockZ() != LootRush.getLootDrop().getCurrentLocation().getBlockZ())
        )
            return;

        event.getPlayer().closeInventory();

        Inventory gui = Bukkit.createInventory(event.getPlayer(), LootRush.getLootDrop().getGuiSize(), LootRush.getLootDrop().getGuiTitle());

        event.getClickedBlock().setType(LootRush.getLootDrop().getPreviousBlock());

        for (ArmorStand a: LootRush.getLootDrop().getArmorStands()) {
            a.remove();
        }

        LootRush.getLootDrop().setArmorStands(new ArrayList<>());

        Random random = new Random();
        ItemStack modifiedItem;
        List<Integer> takenSpots = new ArrayList<>();
        int spots, spot, amount;
        for (LootDropItem i: LootRush.getLootDrop().getItems()) {

            if (i.getSpotsMin() >= i.getSpotsMax()) continue;

            spots = random.nextInt(i.getSpotsMax() - i.getSpotsMin()) + i.getSpotsMin();

            if (spots < 1) continue;

            if (i.getAmountMin() >= i.getAmountMax()) continue;

            for (int j = 0; j < spots; j++) {

                amount = random.nextInt(i.getAmountMax() - i.getAmountMin()) + i.getAmountMin();

                if (amount < 1) continue;

                spot = random.nextInt(gui.getSize());

                while (takenSpots.contains(spot)) {

                    spot = random.nextInt(gui.getSize());

                }

                takenSpots.add(spot);

                modifiedItem = i.getItemStack().clone();
                modifiedItem.setAmount(amount);

                gui.setItem(spot, modifiedItem);

            }

        }

        event.getPlayer().openInventory(gui);

    }

}
