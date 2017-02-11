/*
 * Copyright  2017 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapismc.warppoint;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                generateMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages.yml"));
    }

    public void generateNewPlayerData(File f, Player p) {
        try {
            f.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        YamlConfiguration warps = YamlConfiguration.loadConfiguration(f);
        warps.set("UUID", p.getUniqueId().toString());
        warps.set("UserName", p.getName());
        warps.set("Permission", "NotYetSet");
        warps.set("OfflineSince", "-");
        List<String> sl = new ArrayList<>();
        warps.set("Warps.list", sl);
        plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
    }

    public String getColoredMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', Messages.getString(path));
    }

    public String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
    }

    protected void generateMessages() throws IOException {
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

    protected void reloadMessages(File f) {
        Messages = YamlConfiguration.loadConfiguration(f);
    }

    public YamlConfiguration getPlayerConfig(UUID uuid) {
        if (playerWarps.containsKey(uuid) && playerWarps.get(uuid) != null) {
            return playerWarps.get(uuid);
        } else {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData"
                    + File.separator + uuid.toString() + ".yml");
            if (!f.exists()) {
                return null;
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
