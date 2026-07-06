package fr.chocapiks.hikabrain.listener;

import fr.chocapiks.hikabrain.Hikabrain;
import fr.chocapiks.hikabrain.game.*;
import fr.chocapiks.hikabrain.utils.Schem;
import fr.chocapiks.hikabrain.utils.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Config config = Hikabrain.getGConfig();

        player.getInventory().clear();
        e.joinMessage(Component.text("[Hikabrain] ", NamedTextColor.GOLD)
                .append(Component.text("+ ", NamedTextColor.GREEN))
                .append(Component.text(player.getName(), NamedTextColor.WHITE))
                .append(Component.text(" (" + Bukkit.getOnlinePlayers().size() + "/" + config.getMaxPlayers() + ")", NamedTextColor.GRAY)));

        // No cooldown (Pvp 1.8
        Objects.requireNonNull(player.getAttribute(Attribute.ATTACK_SPEED)).setBaseValue(100000.0);

        GScoreboard scoreboard = Manager.getScoreboard();
        scoreboard.applyTo(player);
        scoreboard.update();

        if(!(Bukkit.getOnlinePlayers().size() > config.getMaxPlayers())) {
            switch (Manager.getPhase()) {
                case Phase.WAITING -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    Manager.addPlayer(player, config.getSmallestTeam());
                    scoreboard.assignPlayerTeam(player, Manager.getTeam(player));

                    if(Bukkit.getOnlinePlayers().size() == config.getMinPlayers()) {
                        if(config.getTimerStart() <= 1) {
                            Manager.setPhase(Phase.PLAYING);
                        } else new Timer(config.getTimerStart(), NamedTextColor.AQUA, "Yeah !").launch();
                    }
                }
                case Phase.STARTING, Phase.PLAYING -> Manager.initSProfil(player);
                case Phase.END -> player.kick(Component.text("Sorry, but this game ended."));
            }
        } else Manager.initSProfil(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        List<Player> inGamePlayer = Manager.getPlayers();
        Config config = Hikabrain.getGConfig();

        e.quitMessage(Component.text("[Hikabrain] ", NamedTextColor.GOLD)
                .append(Component.text("- ", NamedTextColor.RED))
                .append(Component.text(player.getName(), NamedTextColor.WHITE))
                .append(Component.text(" (" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + config.getMaxPlayers() + ")", NamedTextColor.GRAY)));

        if(!inGamePlayer.contains(player)) return ;
        Manager.removePlayer(player);

        switch (Manager.getPhase()) {
            case Phase.STARTING, Phase.PLAYING ->  {
                if(config.getTeams().size() <= 1) Manager.endGame(config.getTeams().getFirst());
            }
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        World world = Bukkit.getWorlds().getFirst();
        world.setTime(6000);
        world.setGameRule(GameRules.ADVANCE_TIME, false);

        FileConfiguration configuration = Hikabrain.getInstance().getConfig();

        int minPlayers = configuration.getInt("game.minPlayers");
        int maxPlayers = configuration.getInt("game.maxPlayers");
        int pointsToWin = configuration.getInt("game.pointsToWin");
        int timerHitDisplay = configuration.getInt("game.timerHitDisplay");
        int timerStart = configuration.getInt("game.timerStart");
        String mapSchem = configuration.getString("game.mapSchem");

        List<Team> teams = new ArrayList<>();
        List<String> configTeams = configuration.getStringList("teams");
        for (String teamColor : configTeams) {
            teams.add(new Team(DyeColor.valueOf(teamColor.toUpperCase())));
        }

        List<Location> locations = new ArrayList<>();
        List<Map<?, ?>> configLocations = configuration.getMapList("locations");
        for (Map<?, ?> map : configLocations) {
            World locWorld = Bukkit.getWorld(map.get("world").toString());
            if (locWorld == null) locWorld = world;

            double x = Double.parseDouble(map.get("x").toString());
            double y = Double.parseDouble(map.get("y").toString());
            double z = Double.parseDouble(map.get("z").toString());
            float yaw = Float.parseFloat(map.get("yaw").toString());
            float pitch = Float.parseFloat(map.get("pitch").toString());

            locations.add(new Location(locWorld, x, y, z, yaw, pitch));
        }

        Schem schem = new Schem(mapSchem);

        Hikabrain.getInstance().setGConfig(new Config(minPlayers, maxPlayers, pointsToWin, timerHitDisplay, timerStart, schem, teams, locations));
        Manager.initTeamsLocation();
    }

}
