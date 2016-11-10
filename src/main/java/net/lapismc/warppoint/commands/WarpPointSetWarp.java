package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpPointSetWarp {

    WarpPoint plugin;

    public WarpPointSetWarp(WarpPoint plugin) {
        this.plugin = plugin;
    }

    public void setWarp(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration warps = plugin.WPConfigs.playerWarps.get(p.getUniqueId());
            if (args.length == 2) {
                String warpName = args[0];
                String type = args[1];
                WarpPoint.WarpType warpType;
                Boolean[] setMove = {false, false};
                switch (type) {
                    case "public":
                        warpType = WarpPoint.WarpType.Public;
                        if (plugin.WPPerms.isPermitted(p, WarpPointPerms.Perm.PublicSet)) {
                            setMove[0] = true;
                        }
                        if (plugin.WPPerms.isPermitted(p, WarpPointPerms.Perm.PublicMove)) {
                            setMove[1] = true;
                        }
                        break;
                    case "private":
                        warpType = WarpPoint.WarpType.Private;
                        if (plugin.WPPerms.isPermitted(p, WarpPointPerms.Perm.Private)) {
                            setMove[0] = true;
                        }
                        break;
                    case "faction":
                        if (!plugin.factions) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.WPConfigs.Messages.getString("FactionsDisabled")));
                            return;
                        }
                        warpType = WarpPoint.WarpType.Faction;
                        if (plugin.WPPerms.isPermitted(p, WarpPointPerms.Perm.FactionSet)) {
                            setMove[0] = true;
                        }
                        if (plugin.WPPerms.isPermitted(p, WarpPointPerms.Perm.FactionMove)) {
                            setMove[1] = true;
                        }
                        break;
                    default:
                        warpType = WarpPoint.WarpType.Private;
                        break;
                }
                if (!setMove[0]) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.WPConfigs.Messages.getString("NoPermission")));
                }
                if (warpName.equalsIgnoreCase("list")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.WPConfigs.Messages.getString("Set.notAvail")));
                }
                if (warpExits(warpName, warpType, p)) {
                    if (!setMove[1]) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.WPConfigs.Messages.getString("Set.notAvail")));
                        return;
                    }
                }
                List<String> warpList = warps.getStringList("Warps.list");
                if (!warpList.contains(warpName)) {
                    warpList.add(warpName);
                    warps.set("Warps.list", warpList);
                }
                warps.set("Warps." + warpName + ".type", warpType.toString());
                warps.set("Warps." + warpName + ".location", plugin.WPConfigs.encodeBase64(p.getLocation()));
                plugin.WPConfigs.playerWarps.put(p.getUniqueId(), warps);
                switch (warpType) {
                    case Public:
                        plugin.WPWarps.addPublicWarp(warpName, p.getUniqueId());
                        break;
                    case Private:
                        plugin.WPWarps.addPrivateWarp(warpName, p.getUniqueId());
                        break;
                    case Faction:
                        plugin.WPFactions.setWarp(p, warpName);
                        break;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.WPConfigs.Messages.getString("Set." + warpType.toString())));
            } else {
                String types;
                if (plugin.factions) {
                    types = "private/public/factions";
                } else {
                    types = "private/public";
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.WPConfigs.Messages.getString("Help.setWarp").replace("%types", types)));
            }
        } else {
            sender.sendMessage(plugin.WPConfigs.Messages.getString("NotAPlayer"));
        }
    }

    private boolean warpExits(String s, WarpPoint.WarpType type, Player p) {
        switch (type) {
            case Public:
                return plugin.WPWarps.publicWarps.containsKey(s);
            case Private:
                return false;
            case Faction:
                return plugin.WPFactions.isWarp(s, p);
            default:
                return true;
        }
    }

}
