package me.mrepiko.lootrush.listeners;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.LootRush;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class PlayerInventoryListener implements Listener {

    @EventHandler
    public void playerInventory(InventoryClickEvent event) {

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) return;

        if (!LootRush.getDisableInventoryActions()) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) event.setCancelled(true);

    }

}
