package me.mrepiko.lootrush.config;

import lombok.Getter;
import org.bukkit.Location;

public class ConfigTeam {

    @Getter private final String color;
    @Getter private final String colorName;
    @Getter private final String colorCode;
    @Getter private final Location location;

    public ConfigTeam(Location location, String color, String colorName, String colorCode) {
        this.location = location;
        this.color = color;
        this.colorName = colorName;
        this.colorCode = colorCode;
    }

    public String toString() {
        return "[" +
                " location: " + this.location.toString() +
                " color: " + this.color +
                " colorName: " + this.colorName +
                " ]";
    }

}
