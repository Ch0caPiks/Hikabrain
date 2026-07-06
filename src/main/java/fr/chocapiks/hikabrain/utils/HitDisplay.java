package fr.chocapiks.hikabrain.utils;

import fr.chocapiks.hikabrain.Hikabrain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class HitDisplay {

    private int leftS;

    public HitDisplay(int seconds) {
        this.leftS = seconds;
    }

    public void start(Location location, double damage) {
        double x = ThreadLocalRandom.current().nextDouble(-2.0, 2.0);
        double y = ThreadLocalRandom.current().nextDouble(-2.0, 2.0);
        double z = ThreadLocalRandom.current().nextDouble(-2.0, 2.0);

        Location rLocation = location.add(x, y, z);

        TextDisplay display = rLocation.getWorld().spawn(rLocation, TextDisplay.class, entity -> {
            entity.text(Component.text("-" + damage).color(NamedTextColor.WHITE));
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setPersistent(false);
            entity.setViewRange(5);
            entity.setTextOpacity((byte) 255);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            entity.setSeeThrough(false);
            entity.setDefaultBackground(false);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                if (leftS <= 0 || display.isDead() || !display.isValid()) {
                    display.remove();
                    cancel();
                    return;
                }

                leftS -= 1;
            }

        }.runTaskTimer(Hikabrain.getInstance(), 0L, 20L);
    }

}
