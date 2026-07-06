package fr.chocapiks.hikabrain.game;

import fr.chocapiks.hikabrain.Hikabrain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GScoreboard {

    private final Scoreboard scoreboard;
    private final Objective objective;

    public GScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();

        this.objective = scoreboard.registerNewObjective(
                "hikabrain",
                "dummy",
                Component.text("HikaBrain", NamedTextColor.GOLD)
        );
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void update() {
        Config config = Hikabrain.getGConfig();

        if (Manager.getPhase() == Phase.WAITING) {
            updateWaiting(config);
        } else if (Manager.getPhase() == Phase.END) {
            updateEnd(config);
        } else {
            updatePlaying(config);
        }
    }

    private void updateWaiting(Config config) {
        clearLines();

        int current = Manager.getPlayers().size();
        int required = config.getMinPlayers();
        int missing = Math.max(0, required - current) - 1;

        setLine("players", 3, Component.text("Players: ", NamedTextColor.WHITE)
                .append(Component.text(current + "/" + required, NamedTextColor.YELLOW)));

        if (missing > 0) {
            setLine("status", 2, Component.text("Waiting for ", NamedTextColor.GRAY)
                    .append(Component.text((missing) + " player" + (missing > 1 ? "s" : ""), NamedTextColor.RED)));
        }

        setLine("blank1", 1, Component.empty());

        setLine("map", 0, Component.text("Map: ", NamedTextColor.WHITE)
                .append(Component.text(config.getSchem().getSchemName().replace(".schem", ""), NamedTextColor.GRAY)));
    }

    private void updatePlaying(Config config) {
        clearLines();

        List<Team> teams = config.getTeams();
        int score = teams.size() + 3;

        setLine("blank1", score + 1, Component.empty());

        for (Team team : teams) {
            if(!(team.getSize() <= 0)) {
                setLine(team.getDyeColor().name(), score, Component.text(team.getDyeColor().name() + ": ", team.getTextColor())
                        .append(Component.text(team.getPoints() + " points", NamedTextColor.GRAY)));
            }
            score--;
        }

        setLine("blank1", 1, Component.empty());

        setLine("Goal", 0, Component.text("Goal: ", NamedTextColor.WHITE)
                .append(Component.text(config.getPointsToWin() + " points", NamedTextColor.GRAY)));
    }

    private void updateEnd(Config config) {
        clearLines();

        List<Team> teams = config.getTeams();
        Team winner = teams.stream()
                .max(Comparator.comparingInt(Team::getPoints))
                .orElse(null);

        setLine("blank1", 2, Component.empty());

        if (winner != null) {
            setLine("winner", 1, Component.text("Winner: ", NamedTextColor.WHITE)
                    .append(Component.text(winner.getDyeColor().name(), winner.getTextColor())));
        }

        setLine("blank2", 0, Component.empty());
    }

    private void setLine(String teamId, int score, Component text) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamId);
        String entry;

        if (team == null) {
            team = scoreboard.registerNewTeam(teamId);
            entry = generateUniqueEntry(teamId);
            team.addEntry(entry);
        } else {
            entry = team.getEntries().iterator().next();
        }

        objective.getScore(entry).setScore(score);
        team.prefix(text);
    }

    private String generateUniqueEntry(String base) {
        return "§" + Integer.toHexString(base.hashCode() & 0xF);
    }

    private void clearLines() {
        for (org.bukkit.scoreboard.Team team : new ArrayList<>(scoreboard.getTeams())) {
            if (!team.getName().startsWith("nick_")) {
                team.unregister();
            }
        }
        for (String entry : scoreboard.getEntries()) {
            if (objective.getScore(entry).isScoreSet()) {
                scoreboard.resetScores(entry);
            }
        }
    }

    public void applyTo(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void assignPlayerTeam(Player player, Team team) {
        for (org.bukkit.scoreboard.Team existing : scoreboard.getTeams()) {
            if (existing.hasEntry(player.getName())) {
                existing.removeEntry(player.getName());
            }
        }

        String teamId = "nick_" + team.getDyeColor().name();
        org.bukkit.scoreboard.Team sbTeam = scoreboard.getTeam(teamId);

        if (sbTeam == null) {
            sbTeam = scoreboard.registerNewTeam(teamId);
            sbTeam.color(NamedTextColor.nearestTo(team.getTextColor()));
            sbTeam.prefix(Component.text(team.getDyeColor().name().charAt(0) + " ", team.getTextColor()).decorate(TextDecoration.BOLD));        }

        sbTeam.addEntry(player.getName());
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}