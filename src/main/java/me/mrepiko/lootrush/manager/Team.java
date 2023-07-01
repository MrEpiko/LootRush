package me.mrepiko.lootrush.manager;

import lombok.Getter;
import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.LootRush;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Team {

    @Getter private final List<Player> players = new ArrayList<>();
    @Getter private int points;
    @Getter private final String color;
    @Getter private final String colorName;
    @Getter private final String colorCode;
    @Getter private final Location location;
    @Getter private final HashMap<String, Integer> coins = new HashMap<>();

    public Team(Player initPlayer, Location location, String color, String colorName, String colorCode) {
        this.players.add(initPlayer);
        this.color = color;
        this.colorName = colorName;
        this.location = location;
        this.colorCode = colorCode;
    }

    public void setPlayerCoins(Player player, int coins) {

        this.coins.put(player.getDisplayName(), coins);

    }

    public int getPlayerCoins(Player player) {

        if (this.coins.containsKey(player.getDisplayName())) {
            return this.coins.get(player.getDisplayName());
        }

        return 0;

    }

    public void addPlayer(Player player) {

        if (LootRush.getEventStatus() != EventStatus.REGISTRATIONS) return;

        if (this.players.contains(player)) return;
        this.players.add(player);
    }

    public void removePlayer(Player player) {

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) return;

        for (Player p: this.players) {

            if (p.getDisplayName().equalsIgnoreCase(player.getDisplayName())) {
                this.players.remove(p);
                break;
            }

        }

    }

    public void setPoints(int points) {

        if (LootRush.getEventStatus() != EventStatus.ACTIVE) return;

        this.points = points;

    }


}
