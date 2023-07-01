package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.SoloPlayer;
import me.mrepiko.lootrush.manager.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.info"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7Event is currently inactive.");
            return true;
        }

        StringBuilder playerOutput = new StringBuilder();

        for (SoloPlayer p: LootRush.getRoundManager().getPlayers().values()) {
            playerOutput
                    .append("§7- ")
                    .append(p.getPlayer().getDisplayName())
                    .append(" (")
                    .append(LootRush.getRoundManager().getPlayers().get(p.getPlayer().getDisplayName()).getPoints())
                    .append(" points, ")
                    .append(LootRush.getRoundManager().getPlayers().get(p.getPlayer().getDisplayName()).getCoins())
                    .append(" coins)")
                    .append("\n");
        }

        StringBuilder teamOutput = new StringBuilder();

        for (Team t: LootRush.getRoundManager().getTeams().values()) {
            teamOutput
                    .append("§7")
                    .append(t.getColorName())
                    .append(" team (")
                    .append(LootRush.getRoundManager().getTeams().get(t.getColor()).getPoints())
                    .append(" points):")
                    .append("\n");
            for (Player p: t.getPlayers()) {
                teamOutput
                        .append("§7- ")
                        .append(p.getDisplayName())
                        .append(" (")
                        .append(t.getPlayerCoins(p))
                        .append(" coins)")
                        .append("\n");
            }
        }

        if (playerOutput.length() == 0) {
            playerOutput.setLength(0);
            playerOutput.append("No players have been registered yet.");
        }

        if (teamOutput.length() == 0) {
            teamOutput.setLength(0);
            teamOutput.append("No teams have been registered yet.");
        }

        player.sendMessage("§e§lLoot§6§lRush §e| §7Status: " + LootRush.getEventStatus().name() + " | Mode: " + LootRush.getEventType().name() + "\n\nPlayers:\n" + playerOutput + "\nTeams:\n" + teamOutput);

        return true;
    }
}
