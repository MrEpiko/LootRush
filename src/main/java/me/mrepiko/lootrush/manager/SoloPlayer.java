package me.mrepiko.lootrush.manager;

import lombok.Getter;
import lombok.Setter;
import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.LootRush;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoloPlayer {

    @Getter private final Player player;
    @Getter @Setter private int points;
    @Getter @Setter private int coins;
    @Getter private final Location location;

    public SoloPlayer(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

}
