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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class WarpPointWarp {

    net.lapismc.warppoint.WarpPoint plugin;

    public WarpPointWarp(net.lapismc.warppoint.WarpPoint p) {
        plugin = p;
    }

    public void warp(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                String warpName = args[0];
                ArrayList<WarpPoint.WarpType> types = new ArrayList<>();
                UUID uuid = p.getUniqueId();
                if (plugin.WPWarps.privateWarps.contains(warpName + ":" + uuid.toString())) {
                    types.add(WarpPoint.WarpType.Private);
                }
                if (plugin.WPWarps.publicWarps.containsKey(warpName)) {
                    types.add(WarpPoint.WarpType.Public);
                }
                if (plugin.factions) {
                    if (plugin.WPFactions.isWarp(warpName, p)) {
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
                            if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perms.PublicTele)) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                            }
                            Location loc = plugin.WPWarps.getPublicWarp(warpName);
                            if (loc != null) {
                                p.teleport(loc);
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                            }
                            break;
                        case Private:
                            if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perms.Private)) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                            }
                            Location loc0 = plugin.WPWarps.getPrivateWarp(warpName, p);
                            if (loc0 != null) {
                                p.teleport(loc0);
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                            }
                            break;
                        case Faction:
                            if (!plugin.factions) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("FactionsDisabled"));
                            }
                            if (!plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perms.FactionTele)) {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                            }
                            if (plugin.WPFactions.isWarp(warpName, p)) {
                                Location loc1 = plugin.WPFactions.getWarp(p, warpName);
                                if (loc1 != null) {
                                    p.teleport(loc1);
                                    p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                } else {
                                    p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                                }
                            } else {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                            }
                            break;
                    }
                }
            } else if (args.length == 2) {
                String warpName = args[0];
                String type = args[1];
                WarpPoint.WarpType warpType;
                switch (type) {
                    case "public":
                        if (plugin.WPWarps.publicWarps.containsKey(warpName)) {
                            Location loc = plugin.WPWarps.getPublicWarp(warpName);
                            if (loc != null) {
                                p.teleport(loc);
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                            }
                        } else {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpDoesntExist"));
                        }
                        break;
                    case "faction":
                        if (plugin.factions) {
                            if (plugin.WPFactions.isWarp(warpName, p)) {
                                Location loc = plugin.WPFactions.getWarp(p, warpName);
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
                        Location loc = plugin.WPWarps.getPrivateWarp(warpName, p);
                        if (loc != null) {
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
