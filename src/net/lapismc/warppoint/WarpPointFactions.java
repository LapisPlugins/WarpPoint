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

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpPointFactions implements Listener {

    public HashMap<Faction, HashMap<String, UUID>> factionWarps = new HashMap<>();
    WarpPoint plugin;

    protected WarpPointFactions(WarpPoint p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        HashMap<String, UUID> fw;
        if (factionWarps.containsKey(f)) {
            fw = factionWarps.get(f);
        } else {
            fw = new HashMap<>();
        }
        fw.put(warpName, uuid);
        factionWarps.put(f, fw);
    }

    public boolean delWarp(Player p, String warpName) {
        if (isWarp(warpName, p)) {
            Faction f = getFaction(p);
            HashMap<String, UUID> map = factionWarps.get(f);
            map.remove(warpName);
            factionWarps.put(f, map);
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(p.getUniqueId());
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warpName);
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warpName + "_" + WarpPoint.WarpType.Faction.toString(), null);
            plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
            return true;
        } else {
            return false;
        }
    }

    public Location getWarp(Player p, String s) {
        Faction f = getFaction(p);
        HashMap<String, UUID> fw = factionWarps.get(f);
        UUID uuid = fw.get(s);
        HashMap<String, UUID> map = factionWarps.get(f);
        UUID uuid0 = map.get(s);
        Location loc = (Location) plugin.WPConfigs.getPlayerConfig(uuid0).get("Warps." + s + "_faction.location");
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

    protected Faction getFaction(UUID uuid) {
        MPlayer fp = MPlayer.get(uuid);
        return fp.getFaction();
    }

    @EventHandler
    public void playerFactionChangeEvent(EventFactionsMembershipChange e) {
        boolean online = e.getMPlayer().isOnline();
        Player p = null;
        if (online) {
            p = e.getMPlayer().getPlayer();
        }
        Faction currentFac = e.getMPlayer().getFaction();
        Faction newFac = e.getNewFaction();
        if (!factionWarps.containsKey(currentFac) || !factionWarps.get(currentFac).containsValue(e.getMPlayer().getUuid())) {
            return;
        }
        HashMap<String, UUID> oldmap = factionWarps.get(currentFac);
        HashMap<String, UUID> newmap;
        if (factionWarps.containsKey(newFac)) {
            newmap = factionWarps.get(newFac);
        } else {
            newmap = new HashMap<>();
        }
        for (String s : oldmap.keySet()) {
            if (oldmap.get(s).equals(e.getMPlayer().getUuid())) {
                if (newmap.containsKey(s)) {
                    if (online) {
                        p.sendMessage("Your factions warp " + s + " was renamed to " + p.getName() + s +
                                " because your new faction already has a faction warp named " + s);
                    }
                    newmap.put(p.getName() + s, e.getMPlayer().getUuid());
                    oldmap.remove(s);
                    YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(e.getMPlayer().getUuid());
                    Location loc = (Location) warps.get("Warps." + s + "_faction.location");
                    warps.set("Warps." + p.getName() + s + "_faction.location", loc);
                    warps.set("Warps." + s + "_faction", null);
                    plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
                }
                newmap.put(s, e.getMPlayer().getUuid());
                oldmap.remove(s);
                if (online) {
                    p.sendMessage("Your warp " + s + "was moved to your new faction");
                }
            }
        }
        factionWarps.put(currentFac, oldmap);
        factionWarps.put(newFac, newmap);
    }

}
