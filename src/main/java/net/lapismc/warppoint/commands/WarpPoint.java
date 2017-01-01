package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPointPerms;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpPoint {

    net.lapismc.warppoint.WarpPoint plugin;

    public WarpPoint(net.lapismc.warppoint.WarpPoint plugin) {
        this.plugin = plugin;
    }

    public void warpPoint(CommandSender sender, String[] args) {
        boolean permitted = false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            permitted = plugin.WPPerms.isPermitted(p, WarpPointPerms.Perms.Admin);
        } else {
            permitted = true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "WarpPoint:" + ChatColor.RED + " v." + plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.GOLD + "Author:" + ChatColor.RED + " dart2112");
            sender.sendMessage(ChatColor.GOLD + "Spigot: " + ChatColor.RED + "https://goo.gl/vUJ8KC");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("update")) {
                if (permitted) {
                    sender.sendMessage(plugin.WPConfigs.coloredMessage("Update.Checking"));
                    if (plugin.lapisUpdater.checkUpdate("WarpPoint")) {
                        sender.sendMessage(plugin.WPConfigs.coloredMessage("Update.Downloading"));
                        plugin.lapisUpdater.downloadUpdate("WarpPoint");
                        sender.sendMessage(plugin.WPConfigs.coloredMessage("Update.Installed"));
                    } else {
                        sender.sendMessage(plugin.WPConfigs.coloredMessage("Update.NoUpdate"));
                    }
                } else {
                    sender.sendMessage(plugin.WPConfigs.coloredMessage("NoPermission"));
                }

            } else if (args[0].equalsIgnoreCase("help")) {
                help(sender);

            } else if (args[0].equalsIgnoreCase("reload")) {
                permitted = false;
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    permitted = plugin.WPPerms.isPermitted(p, WarpPointPerms.Perms.Admin);
                } else {
                    permitted = true;
                }
                if (permitted) {
                    sender.sendMessage("Reloading WarpPoint, you might experience a small lag spike...");
                    plugin.WPConfigs.reloadConfigurations();
                    sender.sendMessage("WarpPoint has been reloaded!");
                } else {
                    sender.sendMessage(plugin.WPConfigs.coloredMessage("NoPermission"));
                }
            } else {
                if (permitted) {
                    sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.warpPointAdmin"));
                } else {
                    sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.warpPoint"));
                }
            }
        }
    }

    private void help(CommandSender sender) {
        boolean admin = false;
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
            admin = plugin.WPPerms.isPermitted(p, WarpPointPerms.Perms.Admin);
        } else {
            admin = true;
        }
        String types;
        if (plugin.factions) {
            types = "private/public/factions";
        } else {
            types = "private/public";
        }
        sender.sendMessage(ChatColor.RED + "--- " + ChatColor.GOLD + "HomeSpawn Help" + ChatColor.RED + " ---");
        sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.warp").replace("%types", types));
        sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.setWarp").replace("%types", types));
        if (admin) {
            sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.delWarpAdmin").replace("%types", types));
            sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.warpPointAdmin"));
        } else {
            sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.delWarp").replace("%types", types));
            sender.sendMessage(plugin.WPConfigs.coloredMessage("Help.warpPoint"));
        }
    }

}
