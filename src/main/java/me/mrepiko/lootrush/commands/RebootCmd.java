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
import org.bukkit.scheduler.BukkitTask;

public class RebootCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.op"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        RoundManagerOutput.MAXIMUM_PLAYERS_REACHED.setMessage(LootRush.getMessages().get("player-cap-reached"));
        RoundManagerOutput.EVENT_DIDNT_START.setMessage(LootRush.getMessages().get("event-not-started"));
        RoundManagerOutput.ALREADY_REGISTERED.setMessage(LootRush.getMessages().get("already-registered"));

        LootRush.setEventStatus(EventStatus.INACTIVE);
        LootRush.setEventType(EventType.NONE);

        LootRush.getRoundManager().clear();
        LootRush.getGeneratorManager().stop();
        LootRush.getLootDrop().stop();

        for (BukkitTask task: Bukkit.getScheduler().getPendingTasks()) {
            if (task.getOwner().equals(LootRush.getINSTANCE())) {
                task.cancel();
            }
        }

        player.sendMessage("§e§lLoot§6§lRush §e| §7Plugin successfully rebooted!");

        return true;
    }
}
