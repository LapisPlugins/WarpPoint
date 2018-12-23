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

package test.warppoint.commands;

import net.lapismc.lapiscore.LapisCoreCommand;
import net.lapismc.warppoint.WarpPointPerms;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WarpPoint extends LapisCoreCommand {

    private final net.lapismc.warppoint.WarpPoint plugin;
    private final WarpPointPlayer WPPlayer;

    public WarpPoint(net.lapismc.warppoint.WarpPoint plugin) {
        super(plugin, "warppoint", "shows plugin info", new ArrayList<>());
        this.plugin = plugin;
        WPPlayer = new WarpPointPlayer(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        boolean permitted;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            permitted = plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Admin);
        } else {
            permitted = true;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.PrimaryColor + "WarpPoint:" + plugin.SecondaryColor + " v." + plugin.getDescription().getVersion());
            sender.sendMessage(plugin.PrimaryColor + "Author:" + plugin.SecondaryColor + " dart2112");
            sender.sendMessage(plugin.PrimaryColor + "Spigot: " + plugin.SecondaryColor + "https://goo.gl/vUJ8KC");
        } else {
            if (args[0].equalsIgnoreCase("update")) {
                if (permitted) {
                    sender.sendMessage(plugin.config.getMessage("Update.Checking"));
                    if (plugin.lapisUpdater.checkUpdate()) {
                        sender.sendMessage(plugin.config.getMessage("Update.Downloading"));
                        plugin.lapisUpdater.downloadUpdate();
                        sender.sendMessage(plugin.config.getMessage("Update.Installed"));
                    } else {
                        sender.sendMessage(plugin.config.getMessage("Update.NoUpdate"));
                    }
                } else {
                    sender.sendMessage(plugin.config.getMessage("NoPermission"));
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                help(sender);
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    permitted = plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Admin);
                }
                if (permitted) {
                    sender.sendMessage("Reloading WarpPoint, you might experience a small lag spike...");
                    plugin.config.reloadConfigurations();
                    sender.sendMessage("WarpPoint has been reloaded!");
                } else {
                    sender.sendMessage(plugin.config.getMessage("NoPermission"));
                }
            } else if (args[0].equalsIgnoreCase("player")) {
                WPPlayer.Player(sender, args, permitted);
            } else {
                if (permitted) {
                    sender.sendMessage(plugin.config.getMessage("Help.warpPointAdmin"));
                } else {
                    sender.sendMessage(plugin.config.getMessage("Help.warpPoint"));
                }
            }
        }
    }

    private void help(CommandSender sender) {
        boolean admin;
        Player p;
        if (sender instanceof Player) {
            p = (Player) sender;
            admin = plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Admin);
        } else {
            admin = true;
        }
        String types;
        if (plugin.factions) {
            types = "private/public/faction";
        } else {
            types = "private/public";
        }
        sender.sendMessage(plugin.SecondaryColor + "--- " + plugin.PrimaryColor + "HomeSpawn Help" + plugin.SecondaryColor + " ---");
        sender.sendMessage(plugin.config.getMessage("Help.warp").replace("%types", types));
        sender.sendMessage(plugin.config.getMessage("Help.setWarp").replace("%types", types));
        if (admin) {
            sender.sendMessage(plugin.config.getMessage("Help.delWarpAdmin").replace("%types", types));
            sender.sendMessage(plugin.config.getMessage("Help.warpPointAdmin"));
        } else {
            sender.sendMessage(plugin.config.getMessage("Help.delWarp").replace("%types", types));
            sender.sendMessage(plugin.config.getMessage("Help.warpPoint"));
        }
    }
}
