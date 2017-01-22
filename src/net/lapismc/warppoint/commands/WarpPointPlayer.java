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
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.*;

public class WarpPointPlayer {

    private WarpPoint plugin;
    private PrettyTime pt = new PrettyTime(Locale.ENGLISH);

    public WarpPointPlayer(WarpPoint p) {
        plugin = p;
        pt.removeUnit(JustNow.class);
        pt.removeUnit(Millisecond.class);
    }

    public void WarpPointPlayer(CommandSender sender, String[] args, Boolean permitted) {
        if (permitted) {
            if (args.length > 1) {
                if (args.length == 2) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                    if (op != null && plugin.WPConfigs.getPlayerConfig(op.getUniqueId()) != null) {
                        YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
                        sender.sendMessage(ChatColor.RED + "--- " + ChatColor.GOLD +
                                "Stats for " + ChatColor.BLUE + op.getName() + ChatColor.RED + " ---");
                        String time;
                        if (op.isOnline()) {
                            Long joinTime = warps.getLong("OnlineSince");
                            if (joinTime == 0) {
                                time = "Unknown";
                                sender.sendMessage(ChatColor.RED + "Online since " + ChatColor.GOLD + time);
                            } else {
                                Date date = new Date(joinTime);
                                time = pt.format(date);
                                sender.sendMessage(ChatColor.RED + "Online since " + ChatColor.GOLD + time);
                            }
                        } else {
                            Long quitTime = warps.getLong("OfflineSince");
                            if (quitTime == 0) {
                                time = "Unknown";
                            } else {
                                Date date = new Date(quitTime);
                                time = pt.format(date);
                                sender.sendMessage(ChatColor.RED + "Offline since " + ChatColor.GOLD + time);
                            }
                        }
                        if (plugin.WPPerms.getPlayerPermission(op.getUniqueId()) != null) {
                            sender.sendMessage(ChatColor.RED + "Player Permission: "
                                    + ChatColor.GOLD + plugin.WPPerms.getPlayerPermission(op.getUniqueId()).getName());
                            List<String> publicWarps = plugin.WPWarps.getOwnedPublicWarps(op.getUniqueId());
                            sender.sendMessage(ChatColor.RED + "Public Warps: " + ChatColor.GOLD + publicWarps.size()
                                    + ChatColor.RED + " of " + ChatColor.GOLD
                                    + plugin.WPPerms.getPermissionValue(op.getUniqueId(),
                                    WarpPointPerms.Perm.PublicWarps) + ChatColor.RED + " used");
                            if (publicWarps.size() > 0) {
                                sender.sendMessage(ChatColor.BLUE +
                                        publicWarps.toString().replace("[", "").replace("]", ""));
                            }
                            List<String> privateWarps = plugin.WPWarps.getPrivateWarps(op.getUniqueId());
                            sender.sendMessage(ChatColor.RED + "Private Warps: " + ChatColor.GOLD
                                    + privateWarps.size() + ChatColor.RED + " of " + ChatColor.GOLD
                                    + plugin.WPPerms.getPermissionValue(op.getUniqueId(),
                                    WarpPointPerms.Perm.Private) + ChatColor.RED + " used");
                            if (privateWarps.size() > 0) {
                                sender.sendMessage(ChatColor.BLUE +
                                        privateWarps.toString().replace("[", "").replace("]", ""));
                            }
                            if (plugin.factions) {
                                List<String> factionWarps = plugin.WPFactions.getOwnedWarps(op.getUniqueId());
                                sender.sendMessage(ChatColor.RED + "Faction Warps: " + ChatColor.GOLD
                                        + factionWarps.size() + ChatColor.RED + " of "
                                        + ChatColor.GOLD + plugin.WPPerms.getPermissionValue(op.getUniqueId(),
                                        WarpPointPerms.Perm.FactionWarps) + ChatColor.RED + " used");
                                if (factionWarps.size() > 0) {
                                    sender.sendMessage(ChatColor.BLUE +
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
                } else if (args.length > 2 && args.length < 5) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
                        return;
                    }
                    Player p = (Player) sender;
                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                    if (op != null && plugin.WPConfigs.getPlayerConfig(op.getUniqueId()) != null) {
                        YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
                        String warpName = args[2];
                        ArrayList<WarpPoint.WarpType> types = new ArrayList<>();
                        UUID uuid = op.getUniqueId();
                        if (plugin.WPWarps.getPrivateWarps(uuid).contains(warpName)) {
                            types.add(WarpPoint.WarpType.Private);
                        }
                        if (plugin.WPWarps.getOwnedPublicWarps(uuid).contains(warpName)) {
                            types.add(WarpPoint.WarpType.Public);
                        }
                        if (plugin.factions) {
                            if (plugin.WPFactions.getFactionWarps(op.getUniqueId()).contains(warpName)) {
                                types.add(WarpPoint.WarpType.Faction);
                            }
                        }
                        if (types.size() > 1 && args.length == 3) {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("TypeNeeded"));
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpPointPlayer"));
                        } else {
                            if (args.length == 4) {
                                switch (args[3].toLowerCase()) {
                                    case "public":
                                        p.teleport(plugin.WPWarps.getPublicWarp(warpName));
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case "private":
                                        p.teleport(plugin.WPWarps.getPrivateWarp(warpName, op.getUniqueId()));
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case "faction":
                                        p.teleport(plugin.WPFactions.getWarp(warpName, op.getUniqueId()));
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    default:
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("InvalidType"));
                                }
                            } else {
                                switch (types.get(0)) {
                                    case Public:
                                        p.teleport(plugin.WPWarps.getPublicWarp(warpName));
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case Private:
                                        p.teleport(plugin.WPWarps.getPrivateWarp(warpName, op.getUniqueId()));
                                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Teleported").replace("%name", warpName));
                                        break;
                                    case Faction:
                                        p.teleport(plugin.WPFactions.getWarp(warpName, op.getUniqueId()));
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
