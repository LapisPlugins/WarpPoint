package net.lapismc.warppoint;

import net.lapismc.warppoint.commands.WarpPointDelWarp;
import net.lapismc.warppoint.commands.WarpPointSetWarp;
import net.lapismc.warppoint.commands.WarpPointWarp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WarpPointCommands implements CommandExecutor {

    WarpPoint plugin;
    WarpPointSetWarp setWarp;
    WarpPointDelWarp delWarp;
    net.lapismc.warppoint.commands.WarpPoint warpPoint;
    WarpPointWarp warp;

    protected WarpPointCommands(WarpPoint p) {
        plugin = p;
        setWarp = new WarpPointSetWarp(plugin);
        delWarp = new WarpPointDelWarp(plugin);
        warpPoint = new net.lapismc.warppoint.commands.WarpPoint(plugin);
        warp = new WarpPointWarp(plugin);

        plugin.getCommand("warp").setExecutor(this);
        plugin.getCommand("setwarp").setExecutor(this);
        plugin.getCommand("delwarp").setExecutor(this);
        plugin.getCommand("warppoint").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
                             String[] args) {
        if (cmd.getName().equalsIgnoreCase("setwarp")) {
            setWarp.setWarp(sender, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("delwarp")) {
            delWarp.delWarp(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("warpPoint")) {
            warpPoint.warpPoint(sender, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("warp")) {
            warp.warp(sender, args);
            return true;
        }
        return false;

    }

}
