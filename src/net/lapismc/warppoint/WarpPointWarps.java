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
            YamlConfiguration yaml = plugin.WPConfigs.getPlayerConfig(uuid);
            Location loc = (Location) yaml.get("Warps." + s + "_" + WarpPoint.WarpType.Private.toString() + ".location");
            return loc;
        } else {
            return null;
        }
    }

    public List<String> getPrivateWarps(UUID uuid) {
        List<String> list = new ArrayList<>();
        for (String s : privateWarps) {
            if (s.contains(uuid.toString())) {
                list.add(s.replace(":" + uuid.toString(), ""));
            }
        }
        return list;
    }

    public boolean removePrivateWarp(UUID uuid, String warpName) {
        if (privateWarps.contains(warpName + ":" + uuid)) {
            privateWarps.remove(warpName + ":" + uuid);
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(uuid);
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warpName + "_" + WarpPoint.WarpType.Private.toString());
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warpName + "_" + WarpPoint.WarpType.Private.toString(), null);
            plugin.WPConfigs.reloadPlayerConfig(uuid, warps);
            return true;
        } else {
            return false;
        }
    }

    public Location getPublicWarp(String s) {
        if (publicWarps.containsKey(s)) {
            UUID uuid = publicWarps.get(s);
            YamlConfiguration yaml = plugin.WPConfigs.getPlayerConfig(uuid);
            Location loc = (Location) yaml.get("Warps." + s + "_" + WarpPoint.WarpType.Public.toString() + ".location");
            return loc;
        } else {
            return null;
        }
    }

    public boolean removePublicWarp(UUID uuid, String warpName) {
        if (publicWarps.get(warpName) != null && publicWarps.get(warpName).equals(uuid)) {
            publicWarps.remove(warpName);
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(uuid);
            List<String> warpsList = warps.getStringList("Warps.list");
            warpsList.remove(warpName + "_" + WarpPoint.WarpType.Private.toString());
            warps.set("Warps.list", warpsList);
            warps.set("Warps." + warpName + "_" + WarpPoint.WarpType.Public.toString(), null);
            plugin.WPConfigs.reloadPlayerConfig(uuid, warps);
            return true;
        } else {
            return false;
        }
    }

}
