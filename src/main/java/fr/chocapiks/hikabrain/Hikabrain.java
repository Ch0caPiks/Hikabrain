package fr.chocapiks.hikabrain;

import fr.chocapiks.hikabrain.game.Config;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public final class Hikabrain extends JavaPlugin {

    private static Hikabrain instance;
    private static Config config;
    private final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerListeners();

        logger.info("Hikabrain has been enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Hikabrain has been disabled!");
    }

    public static Hikabrain getInstance() {
        return instance;
    }

    public static Config getGConfig() {
        return config;
    }

    public void setGConfig(Config gConfig) {
        config = gConfig;
    }

    public @NonNull Logger getLogger() {
        return logger;
    }

    private void registerListeners() {
        String packageName = getClass().getPackage().getName();

        for (Class<?> clazz : new Reflections(packageName).getSubTypesOf(Listener.class)) {
            try {
                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
                getLogger().info("Registered listener: " + clazz.getSimpleName());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                getLogger().severe(e.toString());
            }
        }
    }

}
