package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
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
                switch (type) {
                    case "public":
                        warpType = WarpPoint.WarpType.Public;
                        break;
                    case "private":
                        warpType = WarpPoint.WarpType.Private;
                        break;
                    case "faction":
                        warpType = WarpPoint.WarpType.Faction;
                        break;
                    default:
                        warpType = WarpPoint.WarpType.Private;
                        break;
                }
                if (warpName.equalsIgnoreCase("list") || warpExits(warpName, warpType, p)) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.WPConfigs.Messages.getString("Set.notAvail")));
                }
                List<String> warpList = warps.getStringList("Warps.list");
                if (!warpList.contains(warpName)) {
                    warpList.add(warpName);
                }
                warps.set("Warps." + warpName + ".type", warpType.toString());
                warps.set("Warps." + warpName + ".location", plugin.WPConfigs.encodeBase64(p.getLocation()));
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
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.WPConfigs.Messages.getString("Set." + warpType.toString())));
            } else {
                //help
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
                return plugin.WPWarps.privateWarps.containsKey(s);
            case Faction:
                return plugin.WPFactions.isWarp(s, p);
            default:
                return true;
        }
    }

}
