package fr.chocapiks.hikabrain.game;

import fr.chocapiks.hikabrain.Hikabrain;
import fr.chocapiks.hikabrain.utils.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Manager {

    private static final Hikabrain hikabrain = Hikabrain.getInstance();
    private static final Config config = Hikabrain.getGConfig();
    private static final HashMap<Player, Team> playerTeams = new HashMap<>();
    private static Phase phase = Phase.WAITING;
    private static final GScoreboard scoreboard = new GScoreboard();

    public static List<Player> getPlayers() {
        return new ArrayList<>(playerTeams.keySet());
    }

    public static void addPlayer(Player player, Team team) {
        playerTeams.put(player, team);
        team.setSize(team.getSize() + 1);

        hikabrain.getLogger().info(playerTeams.toString());
        hikabrain.getLogger().info(player.getName() + " joined the team " + getTeam(player));
    }

    public static void removePlayer(Player player) {
        if(!isPlayer(player)) return;
        Team team = playerTeams.get(player);

        team.setSize(team.getSize() -1);
        playerTeams.remove(player);

        if(team.getSize() <= 1) config.getTeams().remove(team);
    }

    public static boolean isPlayer(Player player) {
        if(!getPlayers().contains(player)) {
            hikabrain.getLogger().severe(player + " isn't in GamePlayerList");
            return false;
        } else return true;
    }

    public static Team getTeam(Player player) {
        return playerTeams.get(player);
    }

    public static Phase getPhase() {
        return phase;
    }

    public static void setPhase(Phase newPhase) {
        phase = newPhase;
    }

    public static void initTeamsLocation() {
        List<Location> locations = config.getLocations();

        if (locations.size() < config.getTeams().size()) {
            hikabrain.getLogger().severe("Not enough locations for the teams!");
            return;
        }

        for (int i = 0; i < config.getTeams().size(); i++) {
            config.getTeams().get(i).setSpawnLocation(locations.get(i));
        }

        config.getSchem().placeSchem(new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0));
    }

    public static void endGame(Team winTeam) {
        setPhase(Phase.END);
        scoreboard.update();
        getPlayers().forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            Component victoryTM = Component.text("Team victory: ", NamedTextColor.GRAY)
                    .append(Component.text(getTeam(player).getDyeColor().name(), getTeam(player).getTextColor()))
                    .append(Component.text(" !", NamedTextColor.GRAY));

            if(getTeam(player).equals(winTeam)) {
                player.showTitle(Title.title(Component.text("You won!").color(NamedTextColor.GOLD), victoryTM));
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);
            } else player.showTitle(Title.title(Component.text("You lost!").color(NamedTextColor.RED), victoryTM));
        });

        getPlayers().clear();

        List<Team> teams = new ArrayList<>();
        teams.add(new Team(DyeColor.BLUE));
        teams.add(new Team(DyeColor.RED));
        config.setTeams(teams);
        initTeamsLocation();

        Bukkit.getScheduler().runTaskLater(hikabrain, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> player.kick(Component.text("The game has ended!", NamedTextColor.GOLD)));
            setPhase(Phase.WAITING);
        }, 10 * 20L);
    }

    public static void winMatch(Player player) {
        Team team = getTeam(player);
        team.addPoints(1);
        scoreboard.update();
        Manager.setPhase(Phase.STARTING);

        if(!(team.getPoints() >= config.getPointsToWin())) {
            //loadMap
            config.getSchem().placeSchem(new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0));

            Component message = Component.text("[Hikabrain] ", NamedTextColor.GOLD)
                    .append(Component.text(player.getName(), Manager.getTeam(player).getTextColor()))
                    .append(Component.text(" scored!", NamedTextColor.WHITE));
            Bukkit.getServer().broadcast(message);

            getPlayers().forEach(p -> {
                if (getTeam(p) == team) {
                    p.showTitle(Title.title(Component.text("§6+1 point!"), Component.text("")));
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                } else p.showTitle(Title.title(Component.text(team.getDyeColor().name() + " +1 point").color(team.getTextColor()), Component.text("")));

                initPProfil(p);
            });

            if(!(config.getTimerStart() <= 1)) {
                Bukkit.getScheduler().runTaskLater(hikabrain, () -> {
                    new Timer(config.getTimerStart() - 1, NamedTextColor.AQUA, "Yeah !").launch();
                }, 20L);
            } else Manager.setPhase(Phase.PLAYING);

        } else endGame(team);
    }

    public static void initSProfil(Player player) {
        List<Player> inGamePlayer = Manager.getPlayers();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(inGamePlayer.get(ThreadLocalRandom.current().nextInt(inGamePlayer.size())));

        player.sendMessage(Component.text("You are now in spectator mode!", NamedTextColor.GRAY));
        player.playSound(player.getLocation(), Sound.ENTITY_GHAST_AMBIENT, 1, 1.0f);
        player.showTitle(Title.title(Component.text("SPECTATOR MODE"), Component.text("")));
    }

    public static void initPProfil(Player player) {
        Team team = getTeam(player);
        DyeColor dyeColor = team.getDyeColor();
        Color color = dyeColor.getColor();

        player.teleport(team.getSpawnLocation().clone().add(0, 1, 0));
        player.setHealth(20);
        player.setFoodLevel(20);

        // Apply inventory
        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        ItemStack concrete = new ItemStack(Material.valueOf(dyeColor.name() + "_CONCRETE"), 64);

        ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
        sword.addEnchantment(Enchantment.SHARPNESS, 2);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
        pickaxe.addEnchantment(Enchantment.EFFICIENCY, 5);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(color);
        helmet.setItemMeta(helmetMeta);
        helmet.addEnchantment(Enchantment.PROTECTION, 4);
        helmet.addEnchantment(Enchantment.UNBREAKING, 3);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestMeta.setColor(color);
        chestplate.setItemMeta(chestMeta);
        chestplate.addEnchantment(Enchantment.PROTECTION, 4);
        chestplate.addEnchantment(Enchantment.UNBREAKING, 3);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(color);
        leggings.setItemMeta(leggingsMeta);
        leggings.addEnchantment(Enchantment.PROTECTION, 4);
        leggings.addEnchantment(Enchantment.UNBREAKING, 3);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(color);
        boots.setItemMeta(bootsMeta);
        boots.addEnchantment(Enchantment.PROTECTION, 4);
        boots.addEnchantment(Enchantment.UNBREAKING, 3);

        inventory.setItemInOffHand(concrete);
        inventory.setItem(0, sword);
        inventory.setItem(1, pickaxe);
        inventory.setItem(2, new ItemStack(Material.GOLDEN_APPLE, 64));
        inventory.setItem(3, concrete);
        inventory.setItem(4, concrete);
        inventory.setItem(5, concrete);
        inventory.setItem(6, concrete);
        inventory.setItem(7, concrete);
        inventory.setItem(8, concrete);
        inventory.setHelmet(helmet);
        inventory.setChestplate(chestplate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);
    }

    public static GScoreboard getScoreboard() {
        return scoreboard;
    }
}