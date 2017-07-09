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
import net.lapismc.warppoint.playerdata.Warp;
import net.lapismc.warppoint.playerdata.WarpPointPlayer;
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
        Player player = (Player) sender;
        net.lapismc.warppoint.playerdata.WarpPointPlayer p = new net.lapismc.warppoint.playerdata.WarpPointPlayer(plugin, player);
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
                    types = "private/public/faction";
                } else {
                    types = "private/public";
                }
                p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.delWarp").replace("%types", types));
                return;
            }
            YamlConfiguration warps;
            if (args.length == 3) {
                if (!p.isPermitted(WarpPointPerms.Perm.Admin)) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                    return;
                }
                String pName = args[2];
                //noinspection deprecation
                OfflinePlayer p0 = Bukkit.getOfflinePlayer(pName);
                WarpPointPlayer WPPlayer = new WarpPointPlayer(plugin, p0.getUniqueId());
                warps = WPPlayer.getConfig();
                if (warps == null) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPlayerData"));
                    return;
                }
            }
            Warp warp = plugin.WPWarps.getOwnedWarp(warpName, warpType, p.getUniqueId());
            switch (warpType) {
                case Faction:
                    if (warp != null) {
                        warp.deleteWarp();
                        p.sendMessage(plugin.PrimaryColor + "Removed your faction warp " + plugin.SecondaryColor
                                + warpName);
                    } else {
                        p.sendMessage(plugin.PrimaryColor + "Failed to remove faction warp " + plugin.SecondaryColor
                                + warpName);
                    }
                    break;
                case Public:
                    if (warp != null) {
                        warp.deleteWarp();
                        p.sendMessage(plugin.PrimaryColor + "Removed your public warp " + plugin.SecondaryColor
                                + warpName);
                    } else {
                        p.sendMessage(plugin.PrimaryColor + "Failed to remove public warp " + plugin.SecondaryColor
                                + warpName);
                    }
                    break;
                case Private:
                    if (warp != null) {
                        warp.deleteWarp();
                        p.sendMessage(plugin.PrimaryColor + "Removed your private warp " + plugin.SecondaryColor
                                + warpName);
                    } else {
                        p.sendMessage(plugin.PrimaryColor + "Failed to remove private warp " + plugin.SecondaryColor
                                + warpName);
                    }
                    break;
            }

        } else {
            String types;
            if (plugin.factions) {
                types = "private/public/faction";
            } else {
                types = "private/public";
            }
            p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.delWarp").replace("%types", types));
        }
    }

}
