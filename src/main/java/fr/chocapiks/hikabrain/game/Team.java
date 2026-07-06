package fr.chocapiks.hikabrain.game;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;

public class Team {

    private final DyeColor dyeColor;
    private Integer points = 0;
    private int size;
    private Location spawnLocation;

    public Team(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public TextColor getTextColor() {
        return switch (dyeColor) {
            case ORANGE -> NamedTextColor.GOLD;
            case YELLOW -> NamedTextColor.YELLOW;
            case LIME, GREEN -> NamedTextColor.GREEN;
            case CYAN, LIGHT_BLUE -> NamedTextColor.AQUA;
            case BLUE, PURPLE -> NamedTextColor.BLUE;
            case RED, BROWN -> NamedTextColor.RED;
            case BLACK -> NamedTextColor.BLACK;
            case GRAY -> NamedTextColor.DARK_GRAY;
            case LIGHT_GRAY -> NamedTextColor.GRAY;
            case PINK, MAGENTA -> NamedTextColor.LIGHT_PURPLE;
            default -> NamedTextColor.WHITE;
        };
    }

    public Color getColor() {
        return dyeColor.getColor();
    }

    public Integer getPoints() {
        return points;
    }

    public void addPoints(Integer points) {
        this.points = this.points + points;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
