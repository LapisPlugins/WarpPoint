package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import org.bukkit.command.CommandSender;

public class WarpPointDelWarp {

    private WarpPoint plugin;

    public WarpPointDelWarp(WarpPoint p) {
        this.plugin = p;
    }

    public void delWarp(CommandSender sender, String[] args) {
        WarpPoint.WarpType warpType;
        if (args.length == 2) {
            String warpTypeString = args[0];
            String warpName = args[1];
        } else {

        }
    }

}
