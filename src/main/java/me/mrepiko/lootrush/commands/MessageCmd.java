package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.LootRush;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCmd implements CommandExecutor {


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

        String query = strings[0];

        if (!LootRush.getMessages().containsKey(query)) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7That message doesn't exist in configuration.");
            return true;
        }

        player.sendMessage(LootRush.getMessages().get(query));

        return true;
    }
}
