package net.lapismc.warppoint;

import com.sun.media.jfxmedia.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static javax.swing.UIManager.get;

public class WarpPointConfigurations {

    WarpPoint plugin;
    public HashMap<UUID, YamlConfiguration> playerWarps = new HashMap<>();

    protected WarpPointConfigurations(WarpPoint plugin) {
        this.plugin = plugin;
    }

    protected void generateConfigurations() {
        plugin.saveDefaultConfig();
        if (plugin.getConfig().getInt("ConfigurationVersion") != 1) {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + "config_old.yml");
            File f1 = new File(plugin.getDataFolder().getAbsolutePath() + "config.yml");
            f1.renameTo(f);
            plugin.saveDefaultConfig();
            plugin.logger.info("New Configuration Generated," +
                    " Please Transfer Values From config_old.yml");
        }
        File playerData = new File(plugin.getDataFolder().getAbsolutePath() + "PlayerData");
        if (!playerData.exists()) {
            playerData.mkdir();
        }
        for (File f : playerData.listFiles()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            String uuidString = yaml.getString("UUID");
            UUID uuid = UUID.fromString(uuidString);
            playerWarps.put(uuid, yaml);
        }
        plugin.logger.info("Player Data Files Loaded!");
    }

    public void saveConfigurations() {
        File playerData = new File(plugin.getDataFolder().getAbsolutePath() + "PlayerData");
        for (UUID uuid : playerWarps.keySet()) {
            YamlConfiguration yaml = playerWarps.get(uuid);
            File f = new File(playerData.getAbsolutePath() + uuid.toString() + ".yml");
            try {
                yaml.save(f);
            } catch (IOException e) {
                plugin.logger.info("Failed to save player data for UUID " + uuid.toString());
            }
            playerWarps.remove(uuid);
        }
    }

}
