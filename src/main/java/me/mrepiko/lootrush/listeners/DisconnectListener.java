package me.mrepiko.lootrush.listeners;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.RoundManagerOutput;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconnectListener implements Listener {

    @EventHandler
    public void disconnect(PlayerQuitEvent event) {

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) return;

        if (LootRush.getEventType() == EventType.NONE) return;

        RoundManagerOutput output = null;
        String colorCode = "";

        if (LootRush.getEventType() == EventType.SOLO) {

            if (LootRush.getRoundManager().getSoloPlayer(event.getPlayer()) == null) return;

            output = LootRush.getRoundManager().removePlayer(event.getPlayer());
        } else if (LootRush.getEventType() == EventType.TEAM) {

            if (LootRush.getRoundManager().getTeamOfPlayer(event.getPlayer()) == null) return;

            colorCode = LootRush.getRoundManager().getTeamOfPlayer(event.getPlayer()).getColorCode();
            output = LootRush.getRoundManager().removePlayerFromTeam(event.getPlayer());
        }

        if (output == RoundManagerOutput.SUCCESS) {
            if (!LootRush.getMessages().get("disconnect-message").equalsIgnoreCase(""))
                Bukkit.broadcastMessage(LootRush.getMessages().get("disconnect-message").replace("{player}", event.getPlayer().getDisplayName()).replace("{team_color_code}", colorCode.replace("&", "§")));
        }

    }
}
