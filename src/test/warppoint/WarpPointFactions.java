/*
 * Copyright  2018 Benjamin Martin
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

package test.warppoint;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import net.lapismc.warppoint.playerdata.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpPointFactions implements Listener {

    private final WarpPoint plugin;
    private final HashMap<Faction, HashMap<Warp, UUID>> factionWarps = new HashMap<>();

    WarpPointFactions(WarpPoint p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean isWarp(Warp warp) {
        OfflinePlayer op = warp.getOwner();
        Faction f = getFaction(op.getUniqueId());
        HashMap<Warp, UUID> map = factionWarps.get(f);
        return map != null && map.containsKey(warp);
    }

    public void setWarp(Warp warp) {
        UUID uuid = warp.getOwner().getUniqueId();
        Faction f = getFaction(uuid);
        HashMap<Warp, UUID> fw = factionWarps.get(f);
        if (fw == null) {
            fw = new HashMap<>();
        }
        fw.put(warp, uuid);
        factionWarps.put(f, fw);
    }

    public void deleteWarp(Warp warp) {
        if (isWarp(warp)) {
            OfflinePlayer op = warp.getOwner();
            Faction f = getFaction(warp);
            HashMap<Warp, UUID> map = factionWarps.get(f);
            if (map == null) {
                return;
            }
            map.remove(warp);
            factionWarps.put(f, map);
            YamlConfiguration warps = plugin.config.getPlayerConfig(op.getUniqueId());
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warp.getName() + "_faction");
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warp.getName() + "_" + WarpPoint.WarpType.Faction.toString(), null);
            plugin.config.reloadPlayerConfig(op.getUniqueId(), warps);
        }
    }

    public List<Warp> getOwnedWarps(UUID uuid) {
        List<Warp> list = new ArrayList<>();
        Faction f = getFaction(uuid);
        HashMap<Warp, UUID> fw = factionWarps.get(f);
        if (fw == null) {
            return list;
        }
        for (Warp warp : fw.keySet()) {
            if (fw.get(warp) == uuid) {
                list.add(warp);
            }
        }
        return list;
    }

    public List<Warp> getFactionWarps(UUID uuid) {
        Faction f = getFaction(uuid);
        HashMap<Warp, UUID> fw = factionWarps.get(f);
        List<Warp> warps = new ArrayList<>();
        if (fw == null) {
            return warps;
        }
        warps.addAll(fw.keySet());
        return warps;
    }

    private Faction getFaction(Warp warp) {
        return getFaction(warp.getOwner().getUniqueId());
    }

    private Faction getFaction(UUID uuid) {
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
        HashMap<Warp, UUID> oldmap = factionWarps.get(currentFac);
        HashMap<Warp, UUID> newmap;
        if (factionWarps.containsKey(newFac)) {
            newmap = factionWarps.get(newFac);
        } else {
            newmap = new HashMap<>();
        }
        for (Warp warp : oldmap.keySet()) {
            if (oldmap.get(warp).equals(e.getMPlayer().getUuid())) {
                if (newmap.containsKey(warp)) {
                    if (online) {
                        p.sendMessage(ChatColor.GOLD + "Your factions warp " + ChatColor.RED + warp.getName()
                                + ChatColor.GOLD + " was renamed to " + ChatColor.BLUE + p.getName() + warp.getName() +
                                ChatColor.GOLD + " because your new faction already has a faction warp named "
                                + ChatColor.RED + warp.getName());
                    }
                    YamlConfiguration warps = plugin.config.getPlayerConfig(e.getMPlayer().getUuid());
                    Location loc = (Location) warps.get("Warps." + warp.getName() + "_faction.location");
                    assert p != null;
                    warps.set("Warps." + p.getName() + warp.getName() + "_faction.location", loc);
                    warps.set("Warps." + warp.getName() + "_faction", null);
                    List<String> list = warps.getStringList("Warps.list");
                    list.remove(warp.getName() + "_faction");
                    list.add(p.getName() + warp.getName() + "_faction");
                    warps.set("Warps.list", list);
                    plugin.config.reloadPlayerConfig(p.getUniqueId(), warps);
                    warp.setName(e.getMPlayer().getName() + warp.getName());
                    newmap.put(warp, e.getMPlayer().getUuid());
                    oldmap.remove(warp);

                }
                newmap.put(warp, e.getMPlayer().getUuid());
                oldmap.remove(warp);
                if (online) {
                    p.sendMessage(ChatColor.GOLD + "Your warp " + ChatColor.BLUE + warp.getName() + ChatColor.GOLD
                            + " was moved to your new faction");
                }
            }
        }
        factionWarps.put(currentFac, oldmap);
        factionWarps.put(newFac, newmap);
    }

}
