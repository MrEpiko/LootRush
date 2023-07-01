package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class AllowRegCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.op"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        if (strings.length < 1) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7" + command.getUsage());
            return true;
        }

        EventType wantedStatus = null;
        if (strings[0].toLowerCase(Locale.ROOT).equalsIgnoreCase("solo")) wantedStatus = EventType.SOLO;
        else if (strings[0].toLowerCase(Locale.ROOT).equalsIgnoreCase("team")) wantedStatus = EventType.TEAM;
        else {
            player.sendMessage("§e§lLoot§6§lRush §e| §7That mode does not exist. The available modes are SOLO and TEAM.");
            return true;
        }

        if (LootRush.getEventStatus() != EventStatus.INACTIVE) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7Event is not in inactive state, therefore, this command cannot be executed.");
            return true;
        }

        LootRush.setEventType(wantedStatus);
        LootRush.setEventStatus(EventStatus.REGISTRATIONS);

        player.sendMessage("§e§lLoot§6§lRush §e| §7Successfully opened registrations for " + wantedStatus.name() + "!");

        return true;
    }
}
