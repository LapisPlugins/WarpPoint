package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class WarpPointWarpList {

    private WarpPoint plugin;

    public WarpPointWarpList(WarpPoint p) {
        plugin = p;
    }

    public void warpList(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
            return;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            String types;
            if (plugin.factions) {
                types = "private/public/factions";
            } else {
                types = "private/public";
            }
            p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpList").replace("%types", types));
        } else {
            String typeString = args[0].toLowerCase();
            switch (typeString) {
                case "faction":
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.faction"));
                    List<String> warps0 = plugin.WPFactions.getWarps(p);
                    String warpsString0 = ChatColor.GOLD + warps0.toString().replace("[", "").replace("]", "");
                    p.sendMessage(warpsString0);
                    break;
                case "private":
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.private"));
                    List<String> warps1 = plugin.WPWarps.getPrivateWarps(p.getUniqueId());
                    String warpsString1 = ChatColor.GOLD + warps1.toString().replace("[", "").replace("]", "");
                    p.sendMessage(warpsString1);
                    break;
                case "public":
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.public"));
                    Set<String> warps2 = plugin.WPWarps.publicWarps.keySet();
                    String warpsString2 = ChatColor.GOLD + warps2.toString().replace("[", "").replace("]", "");
                    p.sendMessage(warpsString2);
                    break;
                default:
                    String types;
                    if (plugin.factions) {
                        types = "private/public/factions";
                    } else {
                        types = "private/public";
                    }
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("InvalidType"));
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpList").replace("%types", types));
                    break;
            }
        }
    }

}
