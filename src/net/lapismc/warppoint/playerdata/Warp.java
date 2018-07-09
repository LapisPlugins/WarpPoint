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

package net.lapismc.warppoint.playerdata;

import net.lapismc.warppoint.WarpPoint;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class Warp {

    private final WarpPoint plugin;
    private final WarpPoint.WarpType type;
    private Location l;
    private final OfflinePlayer op;
    private String name;

    /**
     * This should only be used by WarpPoint, please get the object from a players warp lists
     */
    public Warp(WarpPoint plugin, WarpPoint.WarpType type, Location l, OfflinePlayer op, String name) {
        this.plugin = plugin;
        this.type = type;
        this.l = l;
        this.op = op;
        this.name = name;
        if (op != null && op.hasPlayedBefore()) {
            YamlConfiguration warps = plugin.WPConfigs.getPlayerConfig(op.getUniqueId());
            if (!warps.contains("Warps." + name + "_" + type.toString())) {
                warps.set("Warps." + name + "_" + type.toString() + ".location", l);
                plugin.WPConfigs.reloadPlayerConfig(op.getUniqueId(), warps);
            }
        }
    }

    //Methods

    /**
     * Deletes the warp
     */
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

    /**
     * @param p The player you wish to teleport to this warp
     */
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
                //noinspection deprecation
                horse.setPassenger(p);
            }
        } else {
            p.teleport(l);
        }
    }


    //Setters and Getters

    /**
     * @return Returns the warp type
     */
    public WarpPoint.WarpType getType() {
        return type;
    }

    /**
     * @return Returns the location object for the warp
     */
    public Location getLocation() {
        if (l == null) {
            l = (Location) plugin.WPConfigs.getPlayerConfig(op.getUniqueId()).get("Warps." + name + "_" + type.toString() + ".location");
        }
        return l;
    }

    /**
     * @return Returns the offline player object for the owner of the warp
     */
    public OfflinePlayer getOwner() {
        return op;
    }

    /**
     * @return Returns the name of the warp
     */
    public String getName() {
        return name;
    }

    /**
     * This should only be used if you have already edited the name in the user file
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
