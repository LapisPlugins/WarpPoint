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

package test.warppoint.commands;

import net.lapismc.lapiscore.LapisCoreCommand;
import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import net.lapismc.warppoint.api.WarpTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import test.warppoint.playerdata.Warp;

import java.util.ArrayList;

public class WarpPointWarp extends LapisCoreCommand {

    private final net.lapismc.warppoint.WarpPoint plugin;

    public WarpPointWarp(net.lapismc.warppoint.WarpPoint p) {
        super(p, "warp", "sends you to a warp", new ArrayList<>());
        plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
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
                    p.sendMessage(plugin.config.getMessage("TypeNeeded"));
                } else if (types.size() == 0) {
                    p.sendMessage(plugin.config.getMessage("WarpDoesntExist"));
                } else {
                    switch (types.get(0)) {
                        case Public:
                            if (!plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.PublicTele)) {
                                p.sendMessage(plugin.config.getMessage("NoPermission"));
                            }
                            Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Public, p.getUniqueId());
                            WarpTeleportEvent event = new WarpTeleportEvent(p, warp);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                warp.teleportPlayer(p);
                                p.sendMessage(plugin.config.getMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.config.getMessage("ActionCancelled") + event.getCancelReason());
                            }
                            break;
                        case Private:
                            if (!plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Private)) {
                                p.sendMessage(plugin.config.getMessage("NoPermission"));
                            }
                            Warp warp0 = plugin.WPWarps.getOwnedWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId());
                            WarpTeleportEvent event0 = new WarpTeleportEvent(p, warp0);
                            Bukkit.getPluginManager().callEvent(event0);
                            if (!event0.isCancelled()) {
                                warp0.teleportPlayer(p);
                                p.sendMessage(plugin.config.getMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.config.getMessage("ActionCancelled") + event0.getCancelReason());
                            }
                            break;
                        case Faction:
                            if (!plugin.factions) {
                                p.sendMessage(plugin.config.getMessage("FactionsDisabled"));
                            }
                            if (!plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.FactionTele)) {
                                p.sendMessage(plugin.config.getMessage("NoPermission"));
                            }
                            Warp warp1 = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId());
                            WarpTeleportEvent event1 = new WarpTeleportEvent(p, warp1);
                            Bukkit.getPluginManager().callEvent(event1);
                            if (!event1.isCancelled()) {
                                warp1.teleportPlayer(p);
                                p.sendMessage(plugin.config.getMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.config.getMessage("ActionCancelled") + event1.getCancelReason());
                            }
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
                            warp.teleportPlayer(p);
                            p.sendMessage(plugin.config.getMessage("Teleported").replace("%name", warpName));
                        } else {
                            p.sendMessage(plugin.config.getMessage("WarpDoesntExist"));
                        }
                        break;
                    case "faction":
                        if (plugin.factions) {
                            if (plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId()) != null) {
                                Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Faction, p.getUniqueId());
                                warp.teleportPlayer(p);
                                p.sendMessage(plugin.config.getMessage("Teleported").replace("%name", warpName));
                            } else {
                                p.sendMessage(plugin.config.getMessage("WarpDoesntExist"));
                            }
                        } else {
                            p.sendMessage(plugin.config.getMessage("FactionsDisabled"));
                            return;
                        }
                        break;
                    default:
                    case "private":
                        if (plugin.WPWarps.getOwnedWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId()) != null) {
                            Warp warp = plugin.WPWarps.getWarp(warpName, WarpPoint.WarpType.Private, p.getUniqueId());
                            warp.teleportPlayer(p);
                            p.sendMessage(plugin.config.getMessage("Teleported").replace("%name", warpName));
                        } else {
                            p.sendMessage(plugin.config.getMessage("WarpDoesntExist"));
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
                p.sendMessage(plugin.config.getMessage("Help.warp").replace("%types", types));
            }
        } else {
            sender.sendMessage(plugin.config.getMessage("NotAPlayer"));
        }
    }

}
