package net.lapismc.warppoint;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class WarpPointConfigurations {

    public HashMap<UUID, YamlConfiguration> playerWarps = new HashMap<>();
    public YamlConfiguration Messages;
    WarpPoint plugin;

    protected WarpPointConfigurations(WarpPoint plugin) {
        this.plugin = plugin;
    }

    protected void generateConfigurations() {
        plugin.saveDefaultConfig();
        if (plugin.getConfig().getInt("ConfigurationVersion") != 1) {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config_old.yml");
            File f1 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
            f1.renameTo(f);
            plugin.saveDefaultConfig();
            plugin.logger.info("New Configuration Generated," +
                    " Please Transfer Values From config_old.yml");
        }
        File playerData = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
        if (!playerData.exists()) {
            playerData.mkdir();
        }
        File f2 = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages.yml");
        if (!f2.exists()) {
            try {
                f2.createNewFile();
                setMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages.yml"));
    }

    private void setMessages() throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = plugin.getResource("Messages.yml");

            int readBytes;
            byte[] buffer = new byte[4096];
            os = new FileOutputStream(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages.yml");
            while ((readBytes = is.read(buffer)) > 0) {
                os.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public void reloadConfigurations() {
        saveConfigurations();
        loadConfigurations();
    }

    public void reloadPlayerConfig(Player p, YamlConfiguration warps) {
        File warpsFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + p.getUniqueId() + ".yml");
        try {
            warps.save(warpsFile);
            playerWarps.put(p.getUniqueId(), warps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadConfigurations() {
        if (!playerWarps.isEmpty()) {
            saveConfigurations();
        }
        File playerData = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
        File[] files = playerData.listFiles();
        for (File pd : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(pd);
            UUID uuid = UUID.fromString(yaml.getString("UUID"));
            ConfigurationSection cs = yaml.getConfigurationSection("Warps");
            for (String key : cs.getKeys(false)) {
                if (!key.endsWith("list")) {
                    String name = key.replace("Warps.", "");
                    if (key.endsWith("_public")) {
                        plugin.WPWarps.addPublicWarp(name.replace("_public", ""), uuid);
                    }
                    if (key.endsWith("_private")) {
                        if (plugin == null) {
                            System.out.println("Plugin is null");
                            return;
                        }
                        if (plugin.WPWarps == null) {
                            System.out.println("Warps is null");
                            return;
                        }
                        if (name == null) {
                            System.out.println("name is null");
                            return;
                        }
                        plugin.WPWarps.addPrivateWarp(name.replace("_private", ""), uuid);
                    }
                    if (key.endsWith("_faction")) {
                        if (plugin.factions) {
                            plugin.WPFactions.setWarp(uuid, name.replace("_faction", ""));
                        }
                    }
                }
            }
            playerWarps.put(uuid, yaml);
        }
    }


    protected void saveConfigurations() {
        File playerData = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
        for (UUID uuid : playerWarps.keySet()) {
            YamlConfiguration yaml = playerWarps.get(uuid);
            File f = new File(playerData.getAbsolutePath() + File.separator + uuid.toString() + ".yml");
            try {
                yaml.save(f);
            } catch (IOException e) {
                plugin.logger.info("Failed to save player data for UUID " + uuid.toString());
            }
            playerWarps.remove(uuid);
        }
    }

}
