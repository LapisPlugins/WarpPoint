package net.lapismc.warppoint;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class WarpPointConfigurations {

    WarpPoint plugin;
    private HashMap<UUID, YamlConfiguration> playerWarps = new HashMap<>();
    private YamlConfiguration Messages;

    protected WarpPointConfigurations(WarpPoint plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerWarps = new HashMap<>();
            }
        }, 20 * 60 * 5, 20 * 60 * 5);
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

    public String getColoredMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', Messages.getString(path));
    }

    public String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
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

    public YamlConfiguration getPlayerConfig(UUID uuid) {
        if (playerWarps.containsKey(uuid) && playerWarps.get(uuid) != null) {
            return playerWarps.get(uuid);
        } else {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData"
                    + File.separator + uuid.toString() + ".yml");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return YamlConfiguration.loadConfiguration(f);
        }
    }

    public void reloadConfigurations() {
        plugin.logger.info("WarpPoint being reloaded, You may experience a small lag spike");
        saveConfigurations();
        loadConfigurations();
        plugin.logger.info("WarpPoint has been reloaded!");
    }

    public void reloadPlayerConfig(UUID uuid, YamlConfiguration warps) {
        File warpsFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + uuid + ".yml");
        try {
            warps.save(warpsFile);
            playerWarps.put(uuid, warps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void unloadPlayerData(UUID uuid) {
        if (playerWarps.containsKey(uuid)) {
            YamlConfiguration warps = playerWarps.get(uuid);
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData"
                    + File.separator + uuid.toString() + ".yml");
            try {
                warps.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            playerWarps.remove(uuid);
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
