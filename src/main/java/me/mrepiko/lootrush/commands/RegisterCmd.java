package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.RoundManagerOutput;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.op"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        if (strings.length < 1) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7" + command.getUsage());
            return true;
        }

        Player target = Bukkit.getPlayer(strings[0]);

        if (target == null) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7Invalid player.");
            return true;
        }

        if (LootRush.getEventStatus() != EventStatus.REGISTRATIONS) {
            if (!LootRush.getMessages().get("registrations-closed").equalsIgnoreCase(""))
                target.sendMessage(LootRush.getMessages().get("registrations-closed"));
            if (!player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) player.sendMessage("§e§lLoot§6§lRush §e| §7Could not register this player as registrations are closed.");
            return true;
        }

        if (LootRush.getEventType() == EventType.NONE) {
            if (!player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) player.sendMessage("§e§lLoot§6§lRush §e| §7Error occurred while trying to register this player.");
            return true;
        }

        RoundManagerOutput output = (LootRush.getEventType() == EventType.SOLO) ? LootRush.getRoundManager().addPlayer(target) : LootRush.getRoundManager().addPlayerToTeam(target);

        if (output == RoundManagerOutput.SUCCESS) {
            if (!LootRush.getMessages().get("successfully-registered").equalsIgnoreCase(""))
                target.sendMessage(LootRush.getMessages().get("successfully-registered"));
            if (!player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) player.sendMessage("§e§lLoot§6§lRush §e| §7Successfully registered " + target.getDisplayName() + ".");
            return true;
        }

        if (output.getMessage().equalsIgnoreCase("")) return true;

        target.sendMessage(output.getMessage());
        if (!player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) player.sendMessage(output.getMessage());

        return true;
    }
}
