package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import org.bukkit.ChatColor;
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
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.WPConfigs.Messages.getString("TypeNeeded")));
                } else {
                    switch (types.get(1)) {
                        case Public:
                            Location loc = plugin.WPWarps.getPublicWarp(warpName);
                            if (loc != null) {
                                p.teleport(loc);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("Teleported").replace("%name", warpName)));
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                            }
                            break;
                        case Private:
                            Location loc0 = plugin.WPWarps.getPrivateWarp(warpName, p);
                            if (loc0 != null) {
                                p.teleport(loc0);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("Teleported").replace("%name", warpName)));
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                            }
                            break;
                        case Faction:
                            if (plugin.WPFactions.isWarp(warpName, p)) {
                                Location loc1 = plugin.WPFactions.getWarp(p, warpName);
                                if (loc1 != null) {
                                    p.teleport(loc1);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.WPConfigs.Messages.getString("Teleported").replace("%name", warpName)));
                                } else {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                                }
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
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
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("Teleported").replace("%name", warpName)));
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                            }
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                        }
                        break;
                    case "faction":
                        if (plugin.factions) {
                            if (plugin.WPFactions.isWarp(warpName, p)) {
                                Location loc = plugin.WPFactions.getWarp(p, warpName);
                                p.teleport(loc);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("Teleported").replace("%name", warpName)));
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                            }
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.WPConfigs.Messages.getString("FactionsDisabled")));
                            return;
                        }
                        break;
                    default:
                    case "private":
                        Location loc = plugin.WPWarps.getPrivateWarp(warpName, p);
                        if (loc != null) {
                            p.teleport(loc);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.WPConfigs.Messages.getString("Teleported").replace("%name", warpName)));
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.WPConfigs.Messages.getString("WarpDoesntExist")));
                        }
                        break;
                }
            } else {

            }
        }
    }

}
