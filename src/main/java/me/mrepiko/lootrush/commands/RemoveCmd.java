package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.RoundManagerOutput;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.op"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        if (strings.length < 0) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7" + command.getUsage());
            return true;
        }

        Player victim = Bukkit.getPlayer(strings[0]);

        if (victim == null) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7Invalid player.");
            return true;
        }

        RoundManagerOutput output;

        if (LootRush.getEventType() == EventType.SOLO)
            output = LootRush.getRoundManager().removePlayer(victim);
        else if (LootRush.getEventType() == EventType.TEAM)
            output = LootRush.getRoundManager().removePlayerFromTeam(victim);
        else {
            player.sendMessage("§e§lLoot§6§lRush §e| §7Error occurred.");
            return true;
        }

        if (output == RoundManagerOutput.SUCCESS) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7" + victim.getDisplayName() + " §7has been removed from the event.");
            return true;
        }

        if (output.getMessage().equalsIgnoreCase("")) return true;

        player.sendMessage(output.getMessage());
        return true;

    }
}
