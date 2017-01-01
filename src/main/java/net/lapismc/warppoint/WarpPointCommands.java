package net.lapismc.warppoint;

import net.lapismc.warppoint.commands.WarpPointDelWarp;
import net.lapismc.warppoint.commands.WarpPointSetWarp;
import net.lapismc.warppoint.commands.WarpPointWarp;
import net.lapismc.warppoint.commands.WarpPointWarpList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WarpPointCommands implements CommandExecutor {

    WarpPoint plugin;
    WarpPointWarp warp;
    WarpPointSetWarp setWarp;
    WarpPointDelWarp delWarp;
    WarpPointWarpList warpList;
    net.lapismc.warppoint.commands.WarpPoint warpPoint;

    protected WarpPointCommands(WarpPoint p) {
        plugin = p;
        warp = new WarpPointWarp(plugin);
        setWarp = new WarpPointSetWarp(plugin);
        delWarp = new WarpPointDelWarp(plugin);
        warpList = new WarpPointWarpList(plugin);
        warpPoint = new net.lapismc.warppoint.commands.WarpPoint(plugin);

        plugin.getCommand("warp").setExecutor(this);
        plugin.getCommand("setwarp").setExecutor(this);
        plugin.getCommand("delwarp").setExecutor(this);
        plugin.getCommand("warplist").setExecutor(this);
        plugin.getCommand("warppoint").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
                             String[] args) {
        if (cmd.getName().equalsIgnoreCase("warp")) {
            warp.warp(sender, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("setwarp")) {
            setWarp.setWarp(sender, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("delwarp")) {
            delWarp.delWarp(sender, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("warplist")) {
            warpList.warpList(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("warpPoint")) {
            warpPoint.warpPoint(sender, args);
            return true;
        }
        return false;

    }

}
