package net.lapismc.warppoint;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpPointFactions {

    public HashMap<Faction, HashMap<String, UUID>> factionWarps = new HashMap<>();
    WarpPoint plugin;

    protected WarpPointFactions(WarpPoint p) {
        plugin = p;
    }

    public boolean isWarp(String warpName, Player p) {
        Faction f = getFaction(p);
        return factionWarps.get(f).containsKey(warpName);
    }

    public void setWarp(Player p, String warpName) {
        Faction f = getFaction(p);
        UUID uuid = p.getUniqueId();
        HashMap<String, UUID> fw = factionWarps.get(f);
        fw.put(warpName, uuid);
        factionWarps.put(f, fw);
    }

    public void setWarp(UUID uuid, String warpName) {
        MPlayer fp = MPlayer.get(uuid);
        Faction f = fp.getFaction();
        HashMap<String, UUID> fw = factionWarps.get(f);
        fw.put(warpName, uuid);
        factionWarps.put(f, fw);
    }

    public boolean delWarp(Player p, String warpName) {
        if (isWarp(warpName, p)) {
            Faction f = getFaction(p);
            HashMap<String, UUID> map = factionWarps.get(f);
            map.remove(warpName);
            factionWarps.put(f, map);
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

    public Location getWarp(Player p, String s) {
        Faction f = getFaction(p);
        HashMap<String, UUID> fw = factionWarps.get(f);
        UUID uuid = fw.get(s);
        String b64Loc = plugin.WPConfigs.playerWarps.get(uuid).getString("Warps." + s + ".location");
        Location loc = (Location) plugin.WPConfigs.decodeBase64(b64Loc);
        return loc;
    }

    public List<String> getWarps(Player p) {
        Faction f = getFaction(p);
        HashMap<String, UUID> fw = factionWarps.get(f);
        List<String> warps = new ArrayList<>();
        for (String s : fw.keySet()) {
            warps.add(s);
        }
        return warps;
    }

    protected Faction getFaction(Player p) {
        MPlayer fp = MPlayer.get(p);
        return fp.getFaction();
    }

}
