package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WarpPointWarp {

    WarpPoint plugin;

    public WarpPointWarp(WarpPoint plugin) {
        this.plugin = plugin;
    }

    public void warp(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration warps = plugin.WPConfigs.playerWarps.get(p.getUniqueId());
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (args.length == 2) {

                    } else {
                        //too many or too little args
                    }
                }
            } else {
                //help
            }
        } else {

        }
    }

}
