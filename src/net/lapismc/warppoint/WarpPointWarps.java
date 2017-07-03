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

import net.lapismc.warppoint.playerdata.Warp;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class WarpPointWarps {

    WarpPoint plugin;
    private HashMap<Warp, UUID> publicWarps = new HashMap<>();
    private HashMap<UUID, List<Warp>> privateWarps = new HashMap<>();

    WarpPointWarps(WarpPoint p) {
        plugin = p;
    }


    public Warp getWarp(String name, WarpPoint.WarpType type) {
        switch (type) {
            case Public:
                if (publicWarps.size() == 0) {
                    return null;
                }
                for (Warp w : publicWarps.keySet()) {
                    if (w.getName().equalsIgnoreCase(name)) {
                        return w;
                    }
                }
                break;
        }
        return null;
    }

    public Warp getWarp(String name, WarpPoint.WarpType type, UUID uuid) {
        switch (type) {
            case Public:
                if (publicWarps.size() == 0) {
                    return null;
                }
                for (Warp w : publicWarps.keySet()) {
                    if (w.getOwner().getUniqueId().equals(uuid)
                            && w.getName().equalsIgnoreCase(name)) {
                        return w;
                    }
                }
                break;
            case Private:
                if (!privateWarps.containsKey(uuid)) {
                    return null;
                }
                for (Warp w : privateWarps.get(uuid)) {
                    if (w.getOwner().getUniqueId().equals(uuid)
                            && w.getName().equalsIgnoreCase(name)) {
                        return w;
                    }
                }
                break;
            case Faction:
                if (plugin.factions) {
                    if (plugin.WPFactions.getFactionWarps(uuid) == null) {
                        return null;
                    }
                    for (Warp w : plugin.WPFactions.getFactionWarps(uuid)) {
                        if (w.getOwner().getUniqueId().equals(uuid)
                                && w.getName().equalsIgnoreCase(name)) {
                            return w;
                        }
                    }
                }
                break;
        }
        return null;
    }

    public void addPublicWarp(Warp warp) {
        publicWarps.put(warp, warp.getOwner().getUniqueId());
    }

    public void addPrivateWarp(Warp warp) {
        List<Warp> warps;
        UUID uuid = warp.getOwner().getUniqueId();
        if (privateWarps.containsKey(uuid)) {
            warps = privateWarps.get(uuid);
        } else {
            warps = new ArrayList<>();
        }
        warps.add(warp);
        privateWarps.put(uuid, warps);
    }

    public List<Warp> getPrivateWarps(UUID uuid) {
        List<Warp> warpList = new ArrayList<>();
        if (privateWarps.containsKey(uuid)) {
            warpList = privateWarps.get(uuid);
        }
        return warpList;
    }

    public Set<Warp> getAllPublicWarps() {
        return publicWarps.keySet();
    }

    public List<Warp> getOwnedPublicWarps(UUID uuid) {
        List<Warp> warpList = new ArrayList<>();
        for (Warp warp : publicWarps.keySet()) {
            if (publicWarps.get(warp).equals(uuid)) {
                warpList.add(warp);
            }
        }
        return warpList;
    }

    public void removePrivateWarp(Warp warp) {
        UUID uuid = warp.getOwner().getUniqueId();
        if (privateWarps.containsKey(uuid)) {
            privateWarps.remove(uuid);
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(uuid);
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warp.getName() + "_" + WarpPoint.WarpType.Private.toString());
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warp.getName() + "_" + WarpPoint.WarpType.Private.toString(), null);
            plugin.WPConfigs.reloadPlayerConfig(uuid, warps);
        }
    }

    public void removePublicWarp(Warp warp) {
        if (publicWarps.containsKey(warp)) {
            publicWarps.remove(warp);
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(warp.getOwner().getUniqueId());
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warp.getName() + "_" + WarpPoint.WarpType.Private.toString());
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warp.getName() + "_" + WarpPoint.WarpType.Public.toString(), null);
            plugin.WPConfigs.reloadPlayerConfig(warp.getOwner().getUniqueId(), warps);
        }
    }

}
