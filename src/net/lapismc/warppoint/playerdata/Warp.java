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

package net.lapismc.warppoint.playerdata;

import net.lapismc.warppoint.WarpPoint;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class Warp {

    private WarpPoint plugin;
    private WarpPoint.WarpType type;
    private Location l;
    private OfflinePlayer op;
    private String name;

    public Warp(WarpPoint plugin, WarpPoint.WarpType type, Location l, OfflinePlayer op, String name) {
        this.plugin = plugin;
        this.type = type;
        this.l = l;
        this.op = op;
        this.name = name;
        YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
        if (!warps.contains("Warps." + name + "_" + type.toString())) {
            warps.set("Warps." + name + "_" + type.toString() + ".location", l);
            plugin.WPConfigs.reloadPlayerConfig(op.getUniqueId(), warps);
        }
    }

    //Methods

    public void deleteWarp() {
        switch (type) {
            case Public:
                plugin.WPWarps.removePublicWarp(this);
                break;
            case Private:
                plugin.WPWarps.removePrivateWarp(this);
                break;
            case Faction:
                if (plugin.factions) {
                    plugin.WPFactions.deleteWarp(this);
                }
                break;
        }
    }

    public void teleportPlayer(Player p) {
        if (l == null) {
            l = (Location) plugin.WPConfigs.getPlayerConfig(op.getUniqueId()).get("Warps." + name + "_" + type.toString() + ".location");
        }
        if (p.isInsideVehicle()) {
            if (p.getVehicle() instanceof Horse) {
                Horse horse = (Horse) p.getVehicle();
                horse.eject();
                horse.teleport(l);
                p.teleport(l);
                horse.setPassenger(p);
            }
        } else {
            p.teleport(l);
        }
    }


    //Setters and Getters

    public WarpPoint.WarpType getType() {
        return type;
    }

    public void setType(WarpPoint.WarpType type) {
        this.type = type;
    }

    public Location getLocation() {
        return l;
    }

    public void setLocation(Location l) {
        this.l = l;
    }

    public OfflinePlayer getOwner() {
        return op;
    }

    public void setOwner(OfflinePlayer op) {
        this.op = op;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}