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
import net.lapismc.warppoint.api.WarpSetEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import test.warppoint.playerdata.Warp;

import java.util.ArrayList;
import java.util.List;

public class WarpPointSetWarp extends LapisCoreCommand {

    private final WarpPoint plugin;

    public WarpPointSetWarp(WarpPoint plugin) {
        super(plugin, "setwarp", "sets a warp at your location", new ArrayList<>());
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration warps = plugin.config.getPlayerConfig(p.getUniqueId());
            if (args.length == 2) {
                String warpName = args[0];
                String type = args[1];
                WarpPoint.WarpType warpType;
                Boolean[] setMove = {false, false};
                switch (type) {
                    case "public":
                        warpType = WarpPoint.WarpType.Public;
                        if (plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.PublicWarps)) {
                            setMove[0] = true;
                        }
                        if (plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.PublicMove)) {
                            setMove[1] = true;
                        }
                        break;
                    case "private":
                        warpType = WarpPoint.WarpType.Private;
                        if (plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.Private)) {
                            setMove[0] = true;
                        }
                        break;
                    case "faction":
                        if (!plugin.factions) {
                            p.sendMessage(plugin.config.getMessage("FactionsDisabled"));
                            return;
                        }
                        warpType = WarpPoint.WarpType.Faction;
                        if (plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.FactionWarps)) {
                            setMove[0] = true;
                        }
                        if (plugin.perms.isPermitted(p.getUniqueId(), WarpPointPerms.Perm.FactionMove)) {
                            setMove[1] = true;
                        }
                        break;
                    default:
                        warpType = WarpPoint.WarpType.Private;
                        break;
                }
                if (!setMove[0]) {
                    p.sendMessage(plugin.config.getMessage("NoPermission"));
                    return;
                }
                if (warpName.equalsIgnoreCase("list")) {
                    p.sendMessage(plugin.config.getMessage("Set.notAvail"));
                }
                Warp warp = new Warp(plugin, warpType, p.getLocation(),
                        Bukkit.getOfflinePlayer(p.getUniqueId()), warpName);
                if (warpExits(warp)) {
                    if (!setMove[1]) {
                        p.sendMessage(plugin.config.getMessage("Set.noMovePerm"));
                        return;
                    }
                    if (warpType == WarpPoint.WarpType.Public) {
                        warp = plugin.WPWarps.getWarp(warpName, warpType, p.getUniqueId());
                    } else {
                        warp = plugin.WPWarps.getOwnedWarp(warpName, warpType, p.getUniqueId());
                    }
                }
                List<String> warpList = warps.getStringList("Warps.list");
                if (!warpList.contains(warpName + "_" + warpType.toString())) {
                    warpList.add(warpName + "_" + warpType.toString());
                    warps.set("Warps.list", warpList);
                }
                WarpSetEvent event = new WarpSetEvent(warp);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    p.sendMessage(plugin.config.getMessage("ActionCancelled") + event.getCancelReason());
                    return;
                }
                warps.set("Warps." + warpName + "_" + warpType.toString() + ".location", p.getLocation());
                plugin.config.reloadPlayerConfig(p.getUniqueId(), warps);
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
                p.sendMessage(plugin.config.getMessage("Set." + warpType.toString()).replace("%name", warpName));
            } else {
                String types;
                if (plugin.factions) {
                    types = "private/public/faction";
                } else {
                    types = "private/public";
                }
                p.sendMessage(plugin.config.getMessage("Help.setWarp").replace("%types", types));
            }
        } else {
            sender.sendMessage(plugin.config.getMessage("NotAPlayer"));
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
