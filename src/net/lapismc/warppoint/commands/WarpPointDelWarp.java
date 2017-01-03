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

package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WarpPointDelWarp {

    private WarpPoint plugin;

    public WarpPointDelWarp(WarpPoint p) {
        this.plugin = p;
    }

    public void delWarp(CommandSender sender, String[] args) {
        WarpPoint.WarpType warpType;
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
            return;
        }
        Player p = (Player) sender;
        if (args.length >= 2) {
            String warpTypeString = args[1];
            String warpName = args[0];
            switch (warpTypeString.toLowerCase()) {
                case "faction":
                    warpType = WarpPoint.WarpType.Faction;
                    break;
                case "private":
                    warpType = WarpPoint.WarpType.Private;
                    break;
                case "public":
                    warpType = WarpPoint.WarpType.Public;
                    break;
                default:
                    warpType = null;
                    break;
            }
            if (warpType == null) {
                String types;
                if (plugin.factions) {
                    types = "private/public/factions";
                } else {
                    types = "private/public";
                }
                p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.delWarp").replace("%types", types));
                return;
            }
            YamlConfiguration warps;
            if (args.length == 3) {
                if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perms.Admin)) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                    return;
                }
                String pName = args[2];
                OfflinePlayer p0 = Bukkit.getOfflinePlayer(pName);
                warps = plugin.WPConfigs.getPlayerConfig(p0.getUniqueId());
                if (warps == null) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPlayerData"));
                    return;
                }
            } else {
                warps = plugin.WPConfigs.getPlayerConfig(p.getUniqueId());
            }
            if (warps.getStringList("Warps.list").contains(warpName)) {
                switch (warpType) {
                    case Faction:
                        if (plugin.WPFactions.delWarp(p, warpName)) {
                            p.sendMessage("Removed your faction warp " + warpName);
                        } else {
                            p.sendMessage("Failed to remove faction warp " + warpName);
                        }
                        break;
                    case Public:
                        if (plugin.WPWarps.removePublicWarp(p.getUniqueId(), warpName)) {
                            p.sendMessage("Removed your public warp " + warpName);
                        } else {
                            p.sendMessage("Failed to remove public warp " + warpName);
                        }
                        break;
                    case Private:
                        if (plugin.WPWarps.removePrivateWarp(p.getUniqueId(), warpName)) {
                            p.sendMessage("Removed your private warp " + warpName);
                        } else {
                            p.sendMessage("Failed to remove private warp " + warpName);
                        }
                        break;
                }
            } else {
                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
            }
        } else {
            String types;
            if (plugin.factions) {
                types = "private/public/factions";
            } else {
                types = "private/public";
            }
            p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.delWarp").replace("%types", types));
        }
    }

}
