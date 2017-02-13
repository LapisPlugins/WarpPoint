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

package net.lapismc.warppoint.playerdata;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpPointPlayer {

    OfflinePlayer op;
    WarpPoint plugin;

    public WarpPointPlayer(WarpPoint p, UUID uuid) {
        plugin = p;
        op = Bukkit.getOfflinePlayer(uuid);
    }

    public WarpPointPlayer(WarpPoint p, Player player) {
        plugin = p;
        op = Bukkit.getOfflinePlayer(player.getUniqueId());
    }

    public OfflinePlayer getPlayer() {
        return op;
    }

    public UUID getUniqueId() {
        return op.getUniqueId();
    }

    public void sendMessage(String message) {
        if (op.isOnline()) {
            Player p = op.getPlayer();
            p.sendMessage(message);
        }
    }

    public void teleport(Location location) {
        if (op.isOnline()) {
            Player p = op.getPlayer();
            p.teleport(location);
        }
    }

    public YamlConfiguration getConfig() {
        return plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
    }

    public Boolean isPermitted(WarpPointPerms.Perm perm) {
        return plugin.WPPerms.isPermitted(op.getUniqueId(), perm);
    }

    public List<Warp> getAccessibleWarps(WarpPoint.WarpType type) {
        switch (type) {
            case Public:
                List<Warp> warpList = new ArrayList<>();
                for (Warp warp : plugin.WPWarps.getAllPublicWarps()) {
                    warpList.add(warp);
                }
                return warpList;
            case Private:
                return plugin.WPWarps.getPrivateWarps(op.getUniqueId());
            case Faction:
                if (plugin.factions) {
                    return plugin.WPFactions.getFactionWarps(op.getUniqueId());
                }
        }
        return new ArrayList<>();
    }

    public List<Warp> getOwnedWarps(WarpPoint.WarpType type) {
        switch (type) {
            case Private:
                return plugin.WPWarps.getPrivateWarps(op.getUniqueId());
            case Public:
                return plugin.WPWarps.getOwnedPublicWarps(op.getUniqueId());
            case Faction:
                if (plugin.factions) {
                    return plugin.WPFactions.getOwnedWarps(op.getUniqueId());
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

}
