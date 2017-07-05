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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WarpPointWarp {

    private net.lapismc.warppoint.WarpPoint plugin;

    public WarpPointWarp(net.lapismc.warppoint.WarpPoint p) {
        plugin = p;
    }

    public void warp(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                String warpName = args[0];
                ArrayList<WarpPoint.WarpType> types = new ArrayList<>();
                if (plugin.WPWarps.getOwnedWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId()) != null) {
                    types.add(WarpPoint.WarpType.Private);
                }
                if (plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Public, p.getUniqueId()) != null) {
                    types.add(WarpPoint.WarpType.Public);
                }
                if (plugin.factions) {
                    if (plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId()) != null) {
                        types.add(WarpPoint.WarpType.Faction);
                    }
                }
                if (types.size() > 1) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("TypeNeeded"));
                } else if (types.size() == 0) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                } else {
                    switch (types.get(0)) {
                        case Public:
                            if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.PublicTele)) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                            }
                            Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Public, p.getUniqueId());
                            warp.teleportPlayer(p);
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            break;
                        case Private:
                            if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Private)) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                            }
                            Warp warp0 = plugin.WPWarps.getOwnedWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId());
                            warp0.teleportPlayer(p);
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            break;
                        case Faction:
                            if (!plugin.factions) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("FactionsDisabled"));
                            }
                            if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.FactionTele)) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                            }
                            Warp warp1 = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId());
                            warp1.teleportPlayer(p);
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            break;
                    }
                }
            } else if (args.length == 2) {
                String warpName = args[0];
                String type = args[1];
                switch (type) {
                    case "public":
                        if (plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Public, p.getUniqueId()) != null) {
                            Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Public, p.getUniqueId());
                            Location loc = warp.getLocation();
                            p.teleport(loc);
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                        } else {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                        }
                        break;
                    case "faction":
                        if (plugin.factions) {
                            if (plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId()) != null) {
                                Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId());
                                Location loc = warp.getLocation();
                                p.teleport(loc);
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                            }
                        } else {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("FactionsDisabled"));
                            return;
                        }
                        break;
                    default:
                    case "private":
                        if (plugin.WPWarps.getOwnedWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId()) != null) {
                            Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId());
                            Location loc = warp.getLocation();
                            p.teleport(loc);
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                        } else {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                        }
                        break;
                }
            } else {
                p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warp"));
            }
        } else {
            sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
        }
    }

}
