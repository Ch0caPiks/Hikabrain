package fr.chocapiks.hikabrain.listener;

import fr.chocapiks.hikabrain.Hikabrain;
import fr.chocapiks.hikabrain.game.Manager;
import fr.chocapiks.hikabrain.game.Phase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.util.RayTraceResult;

public class Protection implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof Player)) e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        e.setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(Manager.getPhase() != Phase.PLAYING) e.setCancelled(true);
        if(!(Tag.CONCRETE.isTagged(e.getBlock().getType()) || Tag.TERRACOTTA.isTagged(e.getBlock().getType()))) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Location placed = block.getLocation();

        if (Manager.getPhase() != Phase.PLAYING) {
            e.setCancelled(true);
            return;
        }

        boolean nearBed = false;

        int[][] directions = { {1,0,0}, {-1,0,0}, {0,0,1}, {0,0,-1} };
        for (int[] dir : directions) {
            Block adjacent = block.getRelative(dir[0], dir[1], dir[2]);
            if (Tag.BEDS.isTagged(adjacent.getType())) {
                nearBed = true;
                break;
            }
        }

        if (!nearBed) {
            for (int i = 1; i <= 3; i++) {
                Block below = block.getRelative(0, -i, 0);
                if (Tag.BEDS.isTagged(below.getType())) {
                    nearBed = true;
                    break;
                }
            }
        }

        if (nearBed || placed.getBlockY() >= Hikabrain.getGConfig().getLocations().getFirst().getY() + 7) {
            e.getPlayer().sendMessage(Component.text("You can't place blocks here!").color(NamedTextColor.RED));
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        if (Manager.getPhase() == Phase.PLAYING) {
            e.setCancelled(true);
        }
    }
}
