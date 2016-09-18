package net.lapismc.warppoint;

import net.lapismc.warppoint.commands.WarpPointSetWarp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WarpPointCommands implements CommandExecutor {

    WarpPoint plugin;
    WarpPointSetWarp warp;
    net.lapismc.warppoint.commands.WarpPoint warpPoint;

    protected WarpPointCommands(WarpPoint plugin) {
        this.plugin = plugin;
        this.warp = new WarpPointSetWarp(plugin);
        this.warpPoint = new net.lapismc.warppoint.commands.WarpPoint(plugin);

        plugin.getCommand("warp").setExecutor(this);
        plugin.getCommand("warppoint").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
                             String[] args) {
        if (cmd.getName().equalsIgnoreCase("warp")) {
            warp.warp(sender, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("warpPoint")) {
            warpPoint.warpPoint(sender, args);
            return true;
        } else {
            return false;
        }
    }

}
