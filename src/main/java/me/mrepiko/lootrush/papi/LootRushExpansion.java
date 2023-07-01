package me.mrepiko.lootrush.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.SoloPlayer;
import me.mrepiko.lootrush.manager.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LootRushExpansion extends PlaceholderExpansion {

    String[] specialPlaceholders = new String[]{"first", "second", "third", "first_points", "second_points", "third_points"};

    @Override
    public @NotNull String getIdentifier() {
        return "lootrush";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MrEpiko";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        if (player == null) return null;

        if (Arrays.stream(specialPlaceholders).anyMatch(x -> x.equalsIgnoreCase(params))) {

            String first = "N/A";
            String second = "N/A";
            String third = "N/A";
            int firstPoints = 0;
            int secondPoints = 0;
            int thirdPoints = 0;

            if (LootRush.getEventType() == EventType.SOLO) {

                List<SoloPlayer> players = new ArrayList<>(LootRush.getRoundManager().getPlayers().values());

                players.sort(Comparator.comparingInt(SoloPlayer::getPoints).reversed());

                first = (players.size() >= 1) ? players.get(0).getPlayer().getDisplayName() : "N/A";
                firstPoints = (players.size() >= 1) ? players.get(0).getPoints() : 0;

                second = (players.size() >= 2) ? players.get(1).getPlayer().getDisplayName() : "N/A";
                secondPoints = (players.size() >= 2) ? players.get(1).getPoints() : 0;

                third = (players.size() >= 3) ? players.get(2).getPlayer().getDisplayName() : "N/A";
                thirdPoints = (players.size() >= 3) ? players.get(2).getPoints() : 0;

            } else if (LootRush.getEventType() == EventType.TEAM) {

                List<Team> teams = new ArrayList<>(LootRush.getRoundManager().getTeams().values());

                teams.sort(Comparator.comparingInt(Team::getPoints).reversed());

                first = (teams.size() >= 1) ? teams.get(0).getColorName() : "N/A";
                firstPoints = (teams.size() >= 1) ? teams.get(0).getPoints() : 0;

                second = (teams.size() >= 2) ?  teams.get(1).getColorName() : "N/A";
                secondPoints = (teams.size() >= 2) ? teams.get(1).getPoints() : 0;

                third = (teams.size() >= 3) ? teams.get(2).getColorName() : "N/A";
                thirdPoints = (teams.size() >= 3) ? teams.get(2).getPoints() : 0;

            } else {

                first = "N/A";
                second = "N/A";
                third = "N/A";

            }

            switch (params) {

                case "first":
                    return first;

                case "first_points":
                    return String.valueOf(firstPoints);

                case "second":
                    return second;

                case "second_points":
                    return String.valueOf(secondPoints);

                case "third":
                    return third;

                case "third_points":
                    return String.valueOf(thirdPoints);

            }

        }

        switch (params) {

            case "mode":

                if (LootRush.getEventType() == EventType.NONE) return "N/A";
                else if (LootRush.getEventType() == EventType.SOLO) return "Solo";
                else return "Team";

            case "count":

                if (LootRush.getEventType() == EventType.NONE) return "0";
                else if (LootRush.getEventType() == EventType.SOLO) return String.valueOf(LootRush.getRoundManager().getPlayers().size());
                else return String.valueOf(LootRush.getRoundManager().getTeams().size());

            case "mode_word":

                if (LootRush.getEventType() == EventType.TEAM) return "team";
                else return "";

            case "points":

                if (LootRush.getEventType() == EventType.SOLO) {

                    if (LootRush.getRoundManager().getSoloPlayer(player) == null) return "0";
                    else return String.valueOf(LootRush.getRoundManager().getSoloPlayer(player).getPoints());

                } else if (LootRush.getEventType() == EventType.TEAM) {

                    if (LootRush.getRoundManager().getTeamOfPlayer(player) == null) return "N/A";
                    else return String.valueOf(LootRush.getRoundManager().getTeamOfPlayer(player).getPoints());

                } else return "0";

            case "coins":

                if (LootRush.getEventType() == EventType.SOLO) {

                    if (LootRush.getRoundManager().getSoloPlayer(player) == null) return "0";
                    else return String.valueOf(LootRush.getRoundManager().getSoloPlayer(player).getCoins());

                } else if (LootRush.getEventType() == EventType.TEAM) {

                    if (LootRush.getRoundManager().getTeamOfPlayer(player) == null) return "0";
                    else return String.valueOf(LootRush.getRoundManager().getTeamOfPlayer(player).getPlayerCoins(player));

                } else return "0";

            case "base":

                if (LootRush.getEventType() == EventType.SOLO) {

                    if (LootRush.getRoundManager().getSoloPlayer(player) == null) return "N/A";
                    else return LootRush.getRoundManager().getSoloPlayer(player).getLocation().getBlockX() + " " + LootRush.getRoundManager().getSoloPlayer(player).getLocation().getBlockY() + " " + LootRush.getRoundManager().getSoloPlayer(player).getLocation().getBlockZ();

                } else if (LootRush.getEventType() == EventType.TEAM) {

                    if (LootRush.getRoundManager().getTeamOfPlayer(player) == null) return "N/A";
                    else return LootRush.getRoundManager().getTeamOfPlayer(player).getLocation().getBlockX() + " " + LootRush.getRoundManager().getTeamOfPlayer(player).getLocation().getBlockY() + " " + LootRush.getRoundManager().getTeamOfPlayer(player).getLocation().getBlockZ();

                } else return "N/A";

            case "lootdrop":

                if (LootRush.getEventStatus() != EventStatus.ACTIVE) return "N/A";

                return String.valueOf(LootRush.getLootDrop().getSecondsLeft());

        }


        return null;

    }
}
