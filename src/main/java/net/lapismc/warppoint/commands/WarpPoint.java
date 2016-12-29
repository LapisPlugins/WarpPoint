package net.lapismc.warppoint.commands;

import org.bukkit.command.CommandSender;

public class WarpPoint {

    net.lapismc.warppoint.WarpPoint plugin;

    public WarpPoint(net.lapismc.warppoint.WarpPoint plugin) {
        this.plugin = plugin;
    }

    public void warpPoint(CommandSender sender, String[] args) {
        sender.sendMessage("WarpPoint v." + plugin.getDescription().getVersion());
        sender.sendMessage("Author: dart2112");
        sender.sendMessage("Spigot: ");
    }

}
