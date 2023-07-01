package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.RoundManagerOutput;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ShopCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.op"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        if (strings.length == 0) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7" + command.getUsage());
            return true;
        }

        EventType eventType = null;
        if (strings[0].toLowerCase(Locale.ROOT).equalsIgnoreCase("solo")) eventType = EventType.SOLO;
        else if (strings[0].toLowerCase(Locale.ROOT).equalsIgnoreCase("team")) eventType = EventType.TEAM;
        else {
            player.sendMessage("§e§lLoot§6§lRush §e| §7That mode does not exist. The available modes are SOLO and TEAM.");
            return true;
        }

        if (eventType != LootRush.getEventType()) {

            if (!LootRush.getMessages().get("shop-not-available").equalsIgnoreCase(""))
                player.sendMessage(LootRush.getMessages().get("shop-not-available"));
            return true;

        }


        Inventory inv = Bukkit.createInventory(player, LootRush.getShopSize(), LootRush.getShopTitle());

        inv.setContents(LootRush.getShopItems().toArray(new ItemStack[0]));

        player.openInventory(inv);

        return true;
    }
}
