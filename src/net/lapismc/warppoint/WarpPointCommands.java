/*
 * Copyright  2018 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapismc.warppoint;

import net.lapismc.warppoint.commands.WarpPointDelWarp;
import net.lapismc.warppoint.commands.WarpPointSetWarp;
import net.lapismc.warppoint.commands.WarpPointWarp;
import net.lapismc.warppoint.commands.WarpPointWarpList;
import net.lapismc.warppoint.playerdata.WarpPointPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpPointCommands implements TabCompleter {

    private WarpPoint plugin;

    WarpPointCommands(WarpPoint p) {
        plugin = p;
        new WarpPointWarp(plugin);
        new WarpPointSetWarp(plugin);
        new WarpPointDelWarp(plugin);
        new WarpPointWarpList(plugin);
        new net.lapismc.warppoint.commands.WarpPoint(plugin);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        //checks if a player is attempting to tab complete a home name
        if (command.getName().equalsIgnoreCase("warp") || command.getName().equalsIgnoreCase("delwarp")) {
            Player p = (Player) sender;
            YamlConfiguration playerData = new WarpPointPlayer(plugin, p).getConfig();
            //Gets the list of the players homes and returns it for the tab complete to deal with
            List<String> l = new ArrayList<>();
            for (String s : playerData.getStringList("Warps.list")) {
                String warp = s.split("_")[0];
                if (args.length > 0) {
                    if (warp.toLowerCase().startsWith(args[0].toLowerCase())) {
                        l.add(warp);
                    }
                } else {
                    l.add(warp);
                }
            }
            return l;
        }
        return null;
    }

}
