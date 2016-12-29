package net.lapismc.warppoint.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class WarpPoint {

    net.lapismc.warppoint.WarpPoint plugin;

    public WarpPoint(net.lapismc.warppoint.WarpPoint plugin) {
        this.plugin = plugin;
    }

    public void warpPoint(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "WarpPoint:" + ChatColor.RED + " v." + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "Author:" + ChatColor.RED + " dart2112");
        sender.sendMessage(ChatColor.GOLD + "Spigot: " + ChatColor.RED + " ");
    }

}
