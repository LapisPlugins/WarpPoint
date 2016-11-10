package net.lapismc.warppoint;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by benja on 18/09/2016.
 */
public class WarpPointWarps {

    public HashMap<String, UUID> publicWarps = new HashMap<>();
    public ArrayList<String> privateWarps = new ArrayList<>();
    WarpPoint plugin;

    protected WarpPointWarps(WarpPoint p) {
        plugin = p;
    }

    public void addPublicWarp(String s, UUID uuid) {
        publicWarps.put(s, uuid);
    }

    public void addPrivateWarp(String s, UUID uuid) {
        privateWarps.add(s + ":" + uuid.toString());
    }

    public Location getPrivateWarp(String s, Player p) {
        UUID uuid = p.getUniqueId();
        if (privateWarps.contains(s + ":" + uuid)) {
            YamlConfiguration yaml = plugin.WPConfigs.playerWarps.get(uuid);
            Location loc = (Location) plugin.WPConfigs.decodeBase64(yaml.getString("Warps." + s + ".location"));
            return loc;
        } else {
            return null;
        }
    }

    public Location getPublicWarp(String s) {
        if (publicWarps.containsKey(s)) {
            UUID uuid = publicWarps.get(s);
            YamlConfiguration yaml = plugin.WPConfigs.playerWarps.get(uuid);
            Location loc = (Location) plugin.WPConfigs.decodeBase64(yaml.getString("Warps." + s + ".location"));
            return loc;
        } else {
            return null;
        }
    }

}