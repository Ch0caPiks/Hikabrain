package fr.chocapiks.hikabrain.listener;

import fr.chocapiks.hikabrain.Hikabrain;
import fr.chocapiks.hikabrain.game.Config;
import fr.chocapiks.hikabrain.game.Manager;
import fr.chocapiks.hikabrain.game.Phase;
import fr.chocapiks.hikabrain.game.Team;
import fr.chocapiks.hikabrain.utils.HitDisplay;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Mechanics implements Listener {

    @EventHandler
    public void onBedWalk(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if(Manager.getPhase() != Phase.PLAYING) return;
        if(!Manager.isPlayer(player)) return;
        if(!e.hasChangedBlock()) return;

        Material bFoot = e.getTo().getBlock().getType();

        if(!Tag.BEDS.isTagged(bFoot)) return;
        if(bFoot != Material.valueOf(Manager.getTeam(player).getDyeColor().name() + "_BED")) {
            Manager.winMatch(player);
        }
    }

    @EventHandler
    public void onFall(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Config config = Hikabrain.getGConfig();

        if (!e.hasChangedBlock()) return;
        if (e.getTo().getY() < config.getLocations().getFirst().getY() - 15) {
            if (Manager.isPlayer(player)) {
                Manager.initPProfil(player);
                Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f));
            } else player.teleport(config.getLocations().getFirst());
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if(Manager.getPhase() != Phase.PLAYING) return;
        if(e.getEntity() instanceof Player player) {
            Config config = Hikabrain.getGConfig();

            if(config.getTimerStart() <= 1) return;
            new HitDisplay(config.getTimerHitDisplay()).start(player.getLocation(), e.getDamage());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setCancelled(true);

        if (Manager.getPhase() != Phase.PLAYING) return;

        Player player = e.getEntity();
        Player killer = player.getKiller();

        Component message = Component.text("[Hikabrain] ", NamedTextColor.GOLD)
                .append(Component.text(player.getName(), Manager.getTeam(player).getTextColor()))
                .append(Component.text(" a été tué par ", NamedTextColor.WHITE));

        if (killer != null) {
            message = message.append(Component.text(killer.getName(), Manager.getTeam(killer).getTextColor()));
        } else message = message.append(Component.translatable(e.getDamageSource().getDamageType().getTranslationKey(), NamedTextColor.AQUA));

        Bukkit.getServer().broadcast(message);
        Manager.initPProfil(e.getEntity());
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f));
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Player player = e.getPlayer();

        e.renderer((source, sourceDisplayName, message, viewer) -> {
            if (!Manager.isPlayer(player)) {
                return Component.text("[Spectator] ")
                        .color(NamedTextColor.GRAY)
                        .append(sourceDisplayName)
                        .append(Component.text(" » ").color(NamedTextColor.DARK_GRAY))
                        .append(message);
            }

            Team team = Manager.getTeam(player);

            return Component.text("[")
                    .color(NamedTextColor.DARK_GRAY)
                    .append(Component.text(team.getDyeColor().name()).color(team.getTextColor()))
                    .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
                    .append(sourceDisplayName.color(team.getTextColor()))
                    .append(Component.text(" » ").color(NamedTextColor.DARK_GRAY))
                    .append(message.color(NamedTextColor.WHITE));
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(Manager.getPhase() == Phase.STARTING) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Material placedType = e.getBlock().getType();

        if(!Tag.CONCRETE.isTagged(placedType)) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (offHand.getType() == placedType) {
            player.getInventory().setItemInOffHand(new ItemStack(placedType, 64));
        } else if (mainHand.getType() == placedType) {
            player.getInventory().setItemInMainHand(new ItemStack(placedType, 64));
        }
    }
}
