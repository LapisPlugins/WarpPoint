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

import me.kangarko.ui.menu.menues.MenuPagged;
import me.kangarko.ui.model.ItemCreator;
import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.playerdata.Warp;
import net.lapismc.warppoint.playerdata.WarpPointPlayer;
import net.lapismc.warppoint.utils.LapisCommand;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WarpPointWarpList extends LapisCommand {

    private WarpPoint plugin;

    public WarpPointWarpList(WarpPoint p) {
        super("warplist", "shows the warps a player has access to",
                new ArrayList<>(Arrays.asList("warpslist", "listwarp", "listwarps")));
        plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.WPConfigs.getMessage("NotAPlayer"));
            return;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            Set<Warp> warps2 = plugin.WPWarps.getAllPublicWarps();
            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.public"));
            if (warps2.isEmpty()) {
                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoWarpsInList"));
            } else {
                String warpsString2 = plugin.SecondaryColor + warps2.toString().replace("[", "").replace("]", "");
                p.sendMessage(warpsString2);
            }
            List<Warp> warps1 = plugin.WPWarps.getPrivateWarps(p.getUniqueId());
            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.private"));
            if (warps1.isEmpty()) {
                p.sendMessage(plugin.WPConfigs.getColoredMessage("NoWarpsInList"));
            } else {
                String warpsString1 = plugin.SecondaryColor + warps1.toString().replace("[", "").replace("]", "");
                p.sendMessage(warpsString1);
            }
            if (plugin.factions) {
                List<Warp> warps0 = plugin.WPFactions.getFactionWarps(p.getUniqueId());
                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.faction"));
                if (warps0.isEmpty()) {
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("NoWarpsInList"));
                } else {
                    String warpsString0 = plugin.SecondaryColor + warps0.toString().replace("[", "").replace("]", "");
                    p.sendMessage(warpsString0);
                }
            }
        } else {
            String typeString = args[0].toLowerCase();
            WarpPointPlayer player = new WarpPointPlayer(plugin, p.getUniqueId());
            switch (typeString) {
                case "faction":
                    if (plugin.factions) {
                        List<Warp> warps0 = plugin.WPFactions.getFactionWarps(p.getUniqueId());
                        if (warps0.isEmpty()) {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("NoWarpsInList"));
                        } else {
                            if (plugin.getConfig().getBoolean("WarpListGUI")) {
                                new WarpsListUI(player, warps0, WarpPoint.WarpType.Faction).displayTo(p);
                            } else {
                                p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.faction"));
                                String warpsString0 = plugin.SecondaryColor + warps0.toString().replace("[", "").replace("]", "");
                                p.sendMessage(warpsString0);
                            }
                        }
                    } else {
                        p.sendMessage(plugin.WPConfigs.getColoredMessage("FactionsDisabled"));
                    }
                    break;
                case "private":
                    List<Warp> warps1 = plugin.WPWarps.getPrivateWarps(p.getUniqueId());
                    if (warps1.isEmpty()) {
                        p.sendMessage(plugin.WPConfigs.getColoredMessage("NoWarpsInList"));
                    } else {
                        if (plugin.getConfig().getBoolean("WarpListGUI")) {
                            new WarpsListUI(player, warps1, WarpPoint.WarpType.Private).displayTo(p);
                        } else {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.private"));
                            String warpsString1 = plugin.SecondaryColor + warps1.toString().replace("[", "").replace("]", "");
                            p.sendMessage(warpsString1);
                        }
                    }
                    break;
                case "public":
                    Set<Warp> warps2 = plugin.WPWarps.getAllPublicWarps();
                    if (warps2.isEmpty()) {
                        p.sendMessage(plugin.WPConfigs.getColoredMessage("NoWarpsInList"));
                    } else {
                        if (plugin.getConfig().getBoolean("WarpListGUI")) {
                            new WarpsListUI(player, warps2, WarpPoint.WarpType.Public).displayTo(p);
                        } else {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("WarpList.public"));
                            String warpsString2 = plugin.SecondaryColor + warps2.toString().replace("[", "").replace("]", "");
                            p.sendMessage(warpsString2);
                        }
                    }
                    break;
                default:
                    String types;
                    if (plugin.factions) {
                        types = "private/public/faction";
                    } else {
                        types = "private/public";
                    }
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("InvalidType"));
                    p.sendMessage(plugin.WPConfigs.getColoredMessage("Help.warpList").replace("%types", types));
                    break;
            }
        }
    }

    private class WarpsListUI extends MenuPagged<Warp> {

        Random r = new Random(System.currentTimeMillis());
        OfflinePlayer op;
        WarpPoint.WarpType type;

        WarpsListUI(WarpPointPlayer p, Iterable<Warp> warps, WarpPoint.WarpType warpType) {
            super(9 * 2, null, warps);
            op = p.getPlayer();
            type = warpType;
            setTitle(getMenuTitle());
        }

        @Override
        protected String getMenuTitle() {
            return type == null ? "" : "Your " + type.toString() + " warps";
        }

        @Override
        protected ItemStack convertToItemStack(Warp warp) {
            return ItemCreator.of(Material.WOOL).color(DyeColor.values()[(r.nextInt(DyeColor.values().length))])
                    .name(warp.getName()).build().make();
        }

        @Override
        protected void onMenuClickPaged(Player player, Warp warp, ClickType clickType) {
            if (clickType.isLeftClick()) {
                player.closeInventory();
                warp.teleportPlayer(player);
            }
        }

        @Override
        protected boolean updateButtonOnClick() {
            return false;
        }

        @Override
        protected String[] getInfo() {
            return new String[]{
                    "This is a list of your current homes", "", "Left click to teleport!"
            };
        }
    }

}
