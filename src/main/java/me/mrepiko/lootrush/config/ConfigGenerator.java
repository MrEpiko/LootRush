package me.mrepiko.lootrush.config;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ConfigGenerator {

    @Getter private final Location location;
    @Getter private final ItemStack item;
    @Getter private final int points;
    @Getter private final int interval;
    @Getter private final List<String> hologramMessages;

    public ConfigGenerator(Location location, ItemStack item, int points, int interval, List<String> hologramMessages) {
        this.location = location;
        this.item = item;
        this.points = points;
        this.interval = interval;
        Collections.reverse(hologramMessages);
        this.hologramMessages = hologramMessages;
    }

    public String toString() {
        return "[" +
                " location: " + this.location.toString() +
                " material: " + this.item.toString() +
                " points: " + this.points +
                " interval: " + this.interval +
                " hologramMessages: " + this.hologramMessages.toString() +
                " ]";
    }

}
