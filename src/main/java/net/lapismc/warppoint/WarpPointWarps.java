package net.lapismc.warppoint;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpPointWarps {

    public HashMap<String, UUID> publicWarps = new HashMap<>();
    public ArrayList<String> privateWarps = new ArrayList<>();
    WarpPoint plugin;

    protected WarpPointWarps(WarpPoint p) {
        plugin = p;
    }

    public void addPublicWarp(String name, UUID uuid) {
        publicWarps.put(name, uuid);
    }

    public void addPrivateWarp(String name, UUID uuid) {
        privateWarps.add(name + ":" + uuid.toString());
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

    public boolean removePublicWarp(Player p, String warpName) {
        if (publicWarps.get(warpName) != null && publicWarps.get(warpName).equals(p.getUniqueId())) {
            publicWarps.remove(warpName);
            YamlConfiguration warps = plugin.WPConfigs.playerWarps.get(p.getUniqueId());
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warpName);
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warpName, null);
            plugin.WPConfigs.playerWarps.put(p.getUniqueId(), warps);
            return true;
        } else {
            return false;
        }
    }

}
