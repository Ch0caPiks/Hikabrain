package fr.chocapiks.hikabrain.utils;

import fr.chocapiks.hikabrain.Hikabrain;
import fr.chocapiks.hikabrain.game.Manager;
import fr.chocapiks.hikabrain.game.Phase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable {

    private int time;
    private final TextColor textColor;
    private final String finalText;

    public Timer(int time, TextColor textColor, String finalText) {
        this.time = time;
        this.textColor = textColor;
        this.finalText = finalText;
    }

    @Override
    public void run() {
        if(time <= 0) {
            for (Player player : Manager.getPlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                player.setLevel(0);
                player.showTitle(Title.title(Component.text(finalText).color(textColor), Component.text("")));
                Manager.initPProfil(player);
            }
            Manager.setPhase(Phase.PLAYING);
            cancel();
            return;
        }

        for (Player player : Manager.getPlayers()) {
            player.setLevel(time);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
            player.showTitle(Title.title(Component.text(time).color(textColor), Component.text("")));
        }

        Manager.setPhase(Phase.STARTING);
        Manager.getScoreboard().update();

        time--;
    }

    public void launch() {
        this.runTaskTimer(Hikabrain.getInstance(), 0L, 20L);
    }

}
