/*
 * Copyright  2017 Benjamin Martin
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

package net.lapismc.warppoint.commands;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.WarpPointPerms;
import net.lapismc.warppoint.playerdata.Warp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpPointSetWarp {

    WarpPoint plugin;

    public WarpPointSetWarp(WarpPoint plugin) {
        this.plugin = plugin;
    }

    public void setWarp(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(p.getUniqueId());
            if (args.length == 2) {
                String warpName = args[0];
                String type = args[1];
                WarpPoint.WarpType warpType;
                Boolean[] setMove = {false, false};
                switch (type) {
                    case "public":
                        warpType = WarpPoint.WarpType.Public;
                        if (plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.PublicWarps)) {
                            setMove[0] = true;
                        }
                        if (plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.PublicMove)) {
                            setMove[1] = true;
                        }
                        break;
                    case "private":
                        warpType = WarpPoint.WarpType.Private;
                        if (plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Private)) {
                            setMove[0] = true;
                        }
                        break;
                    case "faction":
                        if (!plugin.factions) {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("FactionsDisabled"));
                            return;
                        }
                        warpType = WarpPoint.WarpType.Faction;
                        if (plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.FactionWarps)) {
                            setMove[0] = true;
                        }
                        if (plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.FactionMove)) {
                            setMove[1] = true;
                        }
                        break;
                    default:
                        warpType = WarpPoint.WarpType.Private;
                        break;
                }
                if (!setMove[0]) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("NoPermission"));
                }
                if (warpName.equalsIgnoreCase("list")) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("Set.notAvail"));
                }
                Warp warp = new Warp(plugin, warpType, p.getLocation(),
                        Bukkit.getOfflinePlayer(p.getUniqueId()), warpName);
                if (warpExits(warp)) {
                    if (!setMove[1]) {
                        p.sendMessage(plugin.WPConfigs.getColoredMessage("Set.noMovePerm"));
                        return;
                    }
                }
                List<String> warpList = warps.getStringList("Warps.list");
                if (!warpList.contains(warpName + "_" + warpType.toString())) {
                    warpList.add(warpName + "_" + warpType.toString());
                    warps.set("Warps.list", warpList);
                }
                warps.set("Warps." + warpName + "_" + warpType.toString() + ".location", p.getLocation());
                plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
                switch (warpType) {
                    case Public:
                        plugin.WPWarps.addPublicWarp(warp);
                        break;
                    case Private:
                        plugin.WPWarps.addPrivateWarp(warp);
                        break;
                    case Faction:
                        plugin.WPFactions.setWarp(warp);
                        break;
                }
                p.sendMessage(plugin.WPConfigs.getColoredMessage("Set." + warpType.toString()).replace("%name", warpName));
            } else {
                String types;
                if (plugin.factions) {
                    types = "private/public/faction";
                } else {
                    types = "private/public";
                }
                p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.setWarp").replace("%types", types));
            }
        } else {
            sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
        }
    }

    private boolean warpExits(Warp warp) {
        switch (warp.getType()) {
            case Public:
                return plugin.WPWarps.getOwnedPublicWarps(warp.getOwner().getUniqueId()).contains(warp);
            case Private:
                return false;
            case Faction:
                return plugin.WPFactions.isWarp(warp);
            default:
                return true;
        }
    }

}
