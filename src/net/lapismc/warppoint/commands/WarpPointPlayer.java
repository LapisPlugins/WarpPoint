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

package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import net.lapismc.warppoint.playerdata.Warp;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.*;

import static net.lapismc.warppoint.WarpPoint.WarpType.*;

class WarpPointPlayer {

    private WarpPoint plugin;
    private PrettyTime pt = new PrettyTime(Locale.ENGLISH);

    WarpPointPlayer(WarpPoint p) {
        plugin = p;
        pt.removeUnit(JustNow.class);
        pt.removeUnit(Millisecond.class);
    }

    void Player(CommandSender sender, String[] args, Boolean permitted) {
        if (permitted) {
            if (args.length > 1) {
                if (args.length == 2) {
                    //noinspection deprecation
                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                    if (op != null && plugin.WPConfigs.getPlayerConfig(op.getUniqueId()) != null) {
                        YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
                        sender.sendMessage(plugin.SecondaryColor + "--- " + plugin.PrimaryColor +
                                "Stats for " + plugin.SecondaryColor + op.getName() + plugin.SecondaryColor + " ---");
                        String time;
                        if (op.isOnline()) {
                            Long joinTime = warps.getLong("OnlineSince");
                            if (joinTime == 0) {
                                time = "Unknown";
                                sender.sendMessage(plugin.PrimaryColor + "Online since " + plugin.SecondaryColor + time);
                            } else {
                                Date date = new Date(joinTime);
                                time = pt.format(date);
                                sender.sendMessage(plugin.PrimaryColor + "Online since " + plugin.SecondaryColor + time);
                            }
                        } else {
                            Long quitTime = warps.getLong("OfflineSince");
                            Date date = new Date(quitTime);
                            List<Duration> durationList = pt.calculatePreciseDuration(date);
                            time = pt.format(durationList);
                            sender.sendMessage(plugin.PrimaryColor + "Offline since " + plugin.SecondaryColor + time);
                        }
                        if (plugin.WPPerms.getPlayerPermission(op.getUniqueId()) != null) {
                            sender.sendMessage(plugin.PrimaryColor + "Player Permission: "
                                    + plugin.SecondaryColor + plugin.WPPerms.getPlayerPermission(op.getUniqueId()).getName());
                            List<Warp> publicWarps = plugin.WPWarps.getOwnedPublicWarps(op.getUniqueId());
                            sender.sendMessage(plugin.PrimaryColor + "Public Warps: " + plugin.SecondaryColor + publicWarps.size()
                                    + plugin.PrimaryColor + " of " + plugin.SecondaryColor
                                    + plugin.WPPerms.getPermissionValue(op.getUniqueId(),
                                    WarpPointPerms.Perm.PublicWarps) + plugin.PrimaryColor + " used");
                            if (publicWarps.size() > 0) {
                                sender.sendMessage(plugin.SecondaryColor +
                                        publicWarps.toString().replace("[", "").replace("]", ""));
                            }
                            List<Warp> privateWarps = plugin.WPWarps.getPrivateWarps(op.getUniqueId());
                            sender.sendMessage(plugin.PrimaryColor + "Private Warps: " + plugin.SecondaryColor
                                    + privateWarps.size() + plugin.PrimaryColor + " of " + plugin.SecondaryColor
                                    + plugin.WPPerms.getPermissionValue(op.getUniqueId(),
                                    WarpPointPerms.Perm.Private) + plugin.PrimaryColor + " used");
                            if (privateWarps.size() > 0) {
                                sender.sendMessage(plugin.SecondaryColor +
                                        privateWarps.toString().replace("[", "").replace("]", ""));
                            }
                            if (plugin.factions) {
                                List<Warp> factionWarps = plugin.WPFactions.getOwnedWarps(op.getUniqueId());
                                sender.sendMessage(plugin.PrimaryColor + "Faction Warps: " + plugin.SecondaryColor
                                        + factionWarps.size() + plugin.PrimaryColor + " of "
                                        + plugin.SecondaryColor + plugin.WPPerms.getPermissionValue(op.getUniqueId(),
                                        WarpPointPerms.Perm.FactionWarps) + plugin.PrimaryColor + " used");
                                if (factionWarps.size() > 0) {
                                    sender.sendMessage(plugin.SecondaryColor +
                                            factionWarps.toString().replace("[", "").replace("]", ""));
                                }
                            }
                        }
                    } else {
                        if (sender instanceof Player) {
                            sender.sendMessage(plugin.WPConfigs.getColoredMessage("NoPlayerData"));
                        } else {
                            sender.sendMessage(plugin.WPConfigs.getMessage("NoPlayerData"));
                        }
                    }
                } else if (args.length < 5) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
                        return;
                    }
                    Player p = (Player) sender;
                    //noinspection deprecation
                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                    if (op != null && plugin.WPConfigs.getPlayerConfig(op.getUniqueId()) != null) {
                        String warpName = args[2];
                        ArrayList<WarpPoint.WarpType> types = new ArrayList<>();
                        UUID uuid = op.getUniqueId();
                        if (plugin.WPWarps.getPrivateWarps(uuid).contains(warpName)) {
                            types.add(WarpPoint.WarpType.Private);
                        }
                        if (plugin.WPWarps.getOwnedPublicWarps(uuid).contains(warpName)) {
                            types.add(Public);
                        }
                        if (plugin.factions) {
                            if (plugin.WPFactions.getFactionWarps(op.getUniqueId()).contains(warpName)) {
                                types.add(Faction);
                            }
                        }
                        if (types.size() > 1 && args.length == 3) {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("TypeNeeded"));
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpPointPlayer"));
                        } else {
                            if (args.length == 4) {
                                switch (args[3].toLowerCase()) {
                                    case "public":
                                        plugin.WPWarps.getOwnedWarp(warpName, Public, op.getUniqueId()).teleportPlayer(p);
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case "private":
                                        plugin.WPWarps.getOwnedWarp(warpName, WarpPoint.WarpType.Private, op.getUniqueId()).teleportPlayer(p);
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case "faction":
                                        if (plugin.factions) {
                                            plugin.WPWarps.getOwnedWarp(warpName, Faction, op.getUniqueId()).teleportPlayer(p);
                                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        } else {
                                            p.sendMessage(plugin.WPConfigs.getColoredMessage("FactionsDisabled"));
                                        }
                                        break;
                                    default:
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("InvalidType"));
                                }
                            } else {
                                switch (types.get(0)) {
                                    case Public:
                                        plugin.WPWarps.getOwnedWarp(warpName, Public, op.getUniqueId()).teleportPlayer(p);
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case Private:
                                        plugin.WPWarps.getOwnedWarp(warpName, Private, op.getUniqueId()).teleportPlayer(p);
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case Faction:
                                        plugin.WPWarps.getOwnedWarp(warpName, Faction, op.getUniqueId()).teleportPlayer(p);
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                }
                            }
                        }
                    } else {
                        p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPlayerData"));
                    }
                } else {
                    if (sender instanceof Player) {
                        sender.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpPointPlayer"));
                    } else {
                        sender.sendMessage(plugin.WPConfigs.getMessage("Help.warpPointPlayer"));
                    }
                }
            } else {
                if (sender instanceof Player) {
                    sender.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpPointPlayer"));
                } else {
                    sender.sendMessage(plugin.WPConfigs.getMessage("Help.warpPointPlayer"));
                }
            }
        } else {
            sender.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
        }
    }

}
