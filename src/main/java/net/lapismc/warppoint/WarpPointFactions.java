package net.lapismc.warppoint;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpPointFactions {

    public HashMap<Faction, HashMap<String, UUID>> factionWarps = new HashMap<>();
    public boolean factions;
    WarpPoint plugin;

    protected WarpPointFactions(WarpPoint p) {
        plugin = p;
        try {
            Class.forName("com.massivecraft.factions");
            factions = true;
        } catch (ClassNotFoundException e) {
            factions = false;
        }
    }

    public boolean isFactions() {
        return factions;
    }

    public boolean isWarp(String s, Player p) {
        Faction f = getFaction(p);
        return factionWarps.get(f).containsKey(s);
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

    private Faction getFaction(Player p) {
        MPlayer fp = MPlayer.get(p);
        return fp.getFaction();
    }

}
