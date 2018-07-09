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

package net.lapismc.warppoint.playerdata;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpPointPlayer {

    private final OfflinePlayer op;
    private final WarpPoint plugin;

    public WarpPointPlayer(WarpPoint p, UUID uuid) {
        plugin = p;
        op = Bukkit.getOfflinePlayer(uuid);
    }

    public WarpPointPlayer(WarpPoint p, Player player) {
        plugin = p;
        op = Bukkit.getOfflinePlayer(player.getUniqueId());
    }

    /**
     * @return Returns the OfflinePlayer object of this user
     */
    public OfflinePlayer getPlayer() {
        return op;
    }

    /**
     * @return Returns the players UUID
     */
    public UUID getUniqueId() {
        return op.getUniqueId();
    }

    /**
     * @param message The message you wish to send to the player
     */
    public void sendMessage(String message) {
        if (op.isOnline()) {
            Player p = op.getPlayer();
            p.sendMessage(message);
        }
    }

    /**
     * @return Returns the YAMLConfiguration for this user, only use this if you know what you are doing
     */
    public YamlConfiguration getConfig() {
        return plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
    }

    /**
     * @param perm The permission you wish to check
     * @return returns true if the user has the given permission
     */
    public Boolean isPermitted(WarpPointPerms.Perm perm) {
        return plugin.WPPerms.isPermitted(op.getUniqueId(), perm);
    }

    /**
     * @param type The type of warps you wish to search for
     * @return returns a List of Warps that the player is able to teleport to
     */
    public List<Warp> getAccessibleWarps(WarpPoint.WarpType type) {
        switch (type) {
            case Public:
                return new ArrayList<>(plugin.WPWarps.getAllPublicWarps());
            case Private:
                return plugin.WPWarps.getPrivateWarps(op.getUniqueId());
            case Faction:
                if (plugin.factions) {
                    return plugin.WPFactions.getFactionWarps(op.getUniqueId());
                }
        }
        return new ArrayList<>();
    }

    /**
     * @param type The type of warps you wish to search for
     * @return Returns a List of Warps that the player has set themselves
     */
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
