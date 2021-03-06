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
import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import net.lapismc.warppoint.api.WarpDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import test.warppoint.playerdata.Warp;
import test.warppoint.playerdata.WarpPointPlayer;

import java.util.ArrayList;
import java.util.Collections;

public class WarpPointDelWarp extends LapisCoreCommand {

    private final WarpPoint plugin;

    public WarpPointDelWarp(WarpPoint p) {
        super(p, "delwarp", "deletes a warp", new ArrayList<>(Collections.singletonList("deletewarp")));
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        WarpPoint.WarpType warpType;
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.config.getMessage("NotAPlayer"));
            return;
        }
        Player player = (Player) sender;
        WarpPointPlayer p = new WarpPointPlayer(plugin, player);
        if (args.length >= 2) {
            String warpTypeString = args[1];
            String warpName = args[0];
            switch (warpTypeString.toLowerCase()) {
                case "faction":
                    warpType = WarpPoint.WarpType.Faction;
                    break;
                case "private":
                    warpType = WarpPoint.WarpType.Private;
                    break;
                case "public":
                    warpType = WarpPoint.WarpType.Public;
                    break;
                default:
                    warpType = null;
                    break;
            }
            if (warpType == null) {
                String types;
                if (plugin.factions) {
                    types = "private/public/faction";
                } else {
                    types = "private/public";
                }
                p.sendMessage(plugin.config.getMessage("Help.delWarp").replace("%types", types));
                return;
            }
            YamlConfiguration warps;
            if (args.length == 3) {
                if (!p.isPermitted(WarpPointPerms.Perm.Admin)) {
                    p.sendMessage(plugin.config.getMessage("NoPermission"));
                    return;
                }
                String pName = args[2];
                //noinspection deprecation
                OfflinePlayer p0 = Bukkit.getOfflinePlayer(pName);
                WarpPointPlayer WPPlayer = new WarpPointPlayer(plugin, p0.getUniqueId());
                warps = WPPlayer.getConfig();
                if (warps == null) {
                    p.sendMessage(plugin.config.getMessage("NoPlayerData"));
                    return;
                }
            }
            Warp warp = plugin.WPWarps.getOwnedWarp(warpName, warpType, p.getUniqueId());
            WarpDeleteEvent event = new WarpDeleteEvent(warp);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                p.sendMessage(plugin.config.getMessage("ActionCancelled") + event.getCancelReason());
                return;
            }
            switch (warpType) {
                case Faction:
                    if (warp != null) {
                        warp.deleteWarp();
                        p.sendMessage(plugin.PrimaryColor + "Removed your faction warp " + plugin.SecondaryColor
                                + warpName);
                    } else {
                        p.sendMessage(plugin.PrimaryColor + "Failed to remove faction warp " + plugin.SecondaryColor
                                + warpName);
                    }
                    break;
                case Public:
                    if (warp != null) {
                        warp.deleteWarp();
                        p.sendMessage(plugin.PrimaryColor + "Removed your public warp " + plugin.SecondaryColor
                                + warpName);
                    } else {
                        p.sendMessage(plugin.PrimaryColor + "Failed to remove public warp " + plugin.SecondaryColor
                                + warpName);
                    }
                    break;
                case Private:
                    if (warp != null) {
                        warp.deleteWarp();
                        p.sendMessage(plugin.PrimaryColor + "Removed your private warp " + plugin.SecondaryColor
                                + warpName);
                    } else {
                        p.sendMessage(plugin.PrimaryColor + "Failed to remove private warp " + plugin.SecondaryColor
                                + warpName);
                    }
                    break;
            }

        } else {
            String types;
            if (plugin.factions) {
                types = "private/public/faction";
            } else {
                types = "private/public";
            }
            p.sendMessage(plugin.config.getMessage("Help.delWarp").replace("%types", types));
        }
    }

}
