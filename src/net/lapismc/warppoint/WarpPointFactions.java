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
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        HashMap<String, UUID> map = factionWarps.get(f);
        if (map == null) {
            return false;
        }
        return map.containsKey(warpName);
    }

    public void setWarp(Player p, String warpName) {
        Faction f = getFaction(p);
        UUID uuid = p.getUniqueId();
        HashMap<String, UUID> fw = factionWarps.get(f);
        if (fw == null) {
            fw = new HashMap<>();
        }
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
            if (map == null) {
                return false;
            }
            map.remove(warpName);
            factionWarps.put(f, map);
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(p.getUniqueId());
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warpName + "_faction");
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warpName + "_" + WarpPoint.WarpType.Faction.toString(), null);
            plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
            return true;
        } else {
            return false;
        }
    }

    public Location getWarp(String s, UUID uuid) {
        Faction f = getFaction(uuid);
        HashMap<String, UUID> map = factionWarps.get(f);
        UUID uuid0 = map.get(s);
        Location loc = (Location) plugin.WPConfigs.getPlayerConfig(uuid0).get("Warps." + s + "_faction.location");
        return loc;
    }

    public List<String> getOwnedWarps(UUID uuid) {
        List<String> list = new ArrayList<>();
        Faction f = getFaction(uuid);
        HashMap<String, UUID> fw = factionWarps.get(f);
        if (fw == null) {
            return list;
        }
        for (String s : fw.keySet()) {
            if (fw.get(s) == uuid) {
                list.add(s);
            }
        }
        return list;
    }

    public List<String> getFactionWarps(UUID uuid) {
        Faction f = getFaction(uuid);
        HashMap<String, UUID> fw = factionWarps.get(f);
        List<String> warps = new ArrayList<>();
        if (fw == null) {
            return warps;
        }
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
        Faction newFac;
        if (e.getReason().equals(EventFactionsMembershipChange.MembershipChangeReason.JOIN)
                || e.getReason().equals(EventFactionsMembershipChange.MembershipChangeReason.CREATE)) {
            newFac = e.getNewFaction();
        } else {
            newFac = FactionColl.get().getNone();
        }
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
                        p.sendMessage(ChatColor.GOLD + "Your factions warp " + ChatColor.RED + s
                                + ChatColor.GOLD + " was renamed to " + ChatColor.BLUE + p.getName() + s +
                                ChatColor.GOLD + " because your new faction already has a faction warp named "
                                + ChatColor.RED + s);
                    }
                    newmap.put(p.getName() + s, e.getMPlayer().getUuid());
                    oldmap.remove(s);
                    YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(e.getMPlayer().getUuid());
                    Location loc = (Location) warps.get("Warps." + s + "_faction.location");
                    warps.set("Warps." + p.getName() + s + "_faction.location", loc);
                    warps.set("Warps." + s + "_faction", null);
                    List<String> list = warps.getStringList("Warps.list");
                    list.remove(s + "_faction");
                    list.add(p.getName() + s + "_faction");
                    warps.set("Warps.list", list);
                    plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
                }
                newmap.put(s, e.getMPlayer().getUuid());
                oldmap.remove(s);
                if (online) {
                    p.sendMessage(ChatColor.GOLD + "Your warp " + ChatColor.BLUE + s + ChatColor.GOLD
                            + " was moved to your new faction");
                }
            }
        }
        factionWarps.put(currentFac, oldmap);
        factionWarps.put(newFac, newmap);
    }

}
