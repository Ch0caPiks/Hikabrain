package fr.chocapiks.hikabrain.game;

import fr.chocapiks.hikabrain.Hikabrain;
import fr.chocapiks.hikabrain.utils.Schem;
import org.bukkit.Location;

import java.util.Comparator;
import java.util.List;

public class Config {

    private final int maxPlayers;
    private final int minPlayers; // to switch in GamePhase "STARTING"
    private final int pointsToWin;
    private final int timerHitDisplay;
    private final int timerStart;
    private final Schem schem;
    private List<Team> teams;
    private final List<Location> locations;

    public Config(int minPlayers, int maxPlayers, int pointsToWin, int timerHitDisplay, int timerStart, Schem schem, List<Team> teams, List<Location> locations) {
        if(minPlayers > maxPlayers || minPlayers <= 1) {
            Hikabrain.getInstance().getLogger().severe("minPlayers > maxPlayers");
        }

        if(pointsToWin <= 0) {
            Hikabrain.getInstance().getLogger().severe("pointsToWin <= 0");
        }

        if(teams.size() < 2) {
            Hikabrain.getInstance().getLogger().severe("teams.size() < 2");
        }

        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.pointsToWin = pointsToWin;
        this.timerHitDisplay = timerHitDisplay;
        this.timerStart = timerStart;
        this.schem = schem;
        this.teams = teams;
        this.locations = locations;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getPointsToWin() {
        return pointsToWin;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> t) {
        teams = t;
    }

    public Team getSmallestTeam() {
        return teams.stream().min(Comparator.comparingInt(Team::getSize)).orElse(null);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public int getTimerHitDisplay() {
        return timerHitDisplay;
    }

    public int getTimerStart() {
        return timerStart;
    }

    public Schem getSchem() {
        return schem;
    }

}
