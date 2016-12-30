package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
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
            sender.sendMessage(plugin.WPConfigs.Messages.getString("NotAPlayer"));
            return;
        }
        Player p = (Player) sender;
        if (args.length == 2) {
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
                p.sendMessage(plugin.WPConfigs.coloredMessage("Help.delWarp").replace("%types", types));
                return;
            }
            YamlConfiguration warps = plugin.WPConfigs.playerWarps.get(p.getUniqueId());
            if (warps.getStringList("Warps.list").contains(warpName)
                    || plugin.WPPerms.isPermitted(p, WarpPointPerms.Perms.Admin)) {
                switch (warpType) {
                    case Faction:
                        if (plugin.WPFactions.delWarp(p, warpName)) {
                            p.sendMessage("Removed your faction warp " + warpName);
                        } else {
                            p.sendMessage("Failed to remove faction warp " + warpName);
                        }
                        break;
                    case Public:
                        if (plugin.WPWarps.removePublicWarp(p, warpName)) {
                            p.sendMessage("Removed your public warp " + warpName);
                        } else {
                            p.sendMessage("Failed to remove public warp " + warpName);
                        }
                        break;
                    case Private:
                        if (plugin.WPWarps.removePrivateWarp(p, warpName)) {
                            p.sendMessage("Removed your private warp " + warpName);
                        } else {
                            p.sendMessage("Failed to remove private warp " + warpName);
                        }
                        break;
                }
            } else {
                p.sendMessage(plugin.WPConfigs.coloredMessage("NoPermission"));
            }
        } else {
            String types;
            if (plugin.factions) {
                types = "private/public/factions";
            } else {
                types = "private/public";
            }
            p.sendMessage(plugin.WPConfigs.coloredMessage("Help.delWarp").replace("%types", types));
        }
    }

}
