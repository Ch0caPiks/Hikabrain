package fr.chocapiks.hikabrain.utils;

import fr.chocapiks.hikabrain.Hikabrain;
import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.exception.ParsingException;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public class Schem {

    private final Hikabrain hikabrain = Hikabrain.getInstance();
    private final String schemName;
    private final File schemFolder;

    public Schem(String schemName) {
        this.schemName = schemName;
        this.schemFolder = new File(hikabrain.getDataFolder(), "schems");

        if (!schemFolder.exists()) {
            boolean created = schemFolder.mkdirs();
            if (created) {
                hikabrain.getLogger().info("'schems/' folder successfully created.");
            } else {
                hikabrain.getLogger().warning("Failed to create the 'schems/' folder.");
            }
        }
    }

    public void placeSchem(Location origin) {
        File file = new File(schemFolder, schemName);

        if (!file.exists()) {
            hikabrain.getLogger().warning("File not found: " + file.getPath());
            return;
        }

        Schematic schematic;
        try {
            schematic = SchematicLoader.load(file);
        } catch (ParsingException | IOException e) {
            hikabrain.getLogger().warning("Failed to parse schematic: " + file.getPath());
            return;
        }

        int width = schematic.width();
        int height = schematic.height();
        int length = schematic.length();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    String blockName = schematic.block(x, y, z).name;

                    Location loc = origin.clone().add(x, y, z);
                    loc.getBlock().setBlockData(Bukkit.createBlockData(blockName));
                }
            }
        }
    }

    public String getSchemName() {
        return schemName;
    }
}