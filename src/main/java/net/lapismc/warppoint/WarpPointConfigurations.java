package net.lapismc.warppoint;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Base64;
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
        File f2 = new File(plugin.getDataFolder().getAbsolutePath() + "Messages.yml");
        if (!f2.exists()) {
            try {
                f2.createNewFile();
                setMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsolutePath() + "Messages.yml"));
    }

    private void setMessages() throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = plugin.getResource("Messages.yml");

            int readBytes;
            byte[] buffer = new byte[4096];
            os = new FileOutputStream(plugin.getDataFolder().getAbsolutePath() + "Messages.yml");
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

    public String encodeBase64(Object o) {
        String ed = o.toString();
        byte[] encodedBytes = Base64.getEncoder().encode(ed.getBytes());
        String str = null;
        try {
            str = new String(encodedBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public Object decodeBase64(String s) {
        try {
            byte[] decoded = Base64.getDecoder().decode(s.getBytes("UTF-8"));
            ByteArrayInputStream in = new ByteArrayInputStream(decoded);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void reloadConfigurations() {
        saveConfigurations();
        loadConfigurations();
    }

    protected void loadConfigurations() {
        if (!playerWarps.isEmpty()) {
            saveConfigurations();
        }
        File playerData = new File(plugin.getDataFolder().getAbsolutePath() + "PlayerData");
        File[] files = playerData.listFiles();
        for (File pd : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(pd);
            UUID uuid = UUID.fromString(yaml.getString("UUID"));
            ConfigurationSection cs = yaml.getConfigurationSection("Warps");
            for (String key : cs.getKeys(false)) {
                if (!key.endsWith("list")) {
                    String[] nameArray = key.split(".");
                    String name = nameArray[nameArray.length - 1];
                    String type = yaml.getString(key + ".type");
                    switch (type) {
                        case "public":
                            plugin.WPWarps.addPublicWarp(name, uuid);
                            break;
                        case "private":
                            plugin.WPWarps.addPrivateWarp(name, uuid);
                            break;
                        case "faction":
                            if (plugin.factions) {
                                plugin.WPFactions.setWarp(uuid, name);
                            }
                            break;
                    }
                }
            }
            playerWarps.put(uuid, yaml);
        }
    }

    protected void saveConfigurations() {
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
