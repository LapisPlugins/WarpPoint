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

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An object to store the location and details about a warp
 */

public class Warp {

    private String name;
    private Location loc;
    private WarpType type;
    private WarpPointPlayer owner;

    public Warp(String name, Location loc, WarpType type, WarpPointPlayer owner) {
        this.name = name;
        this.loc = loc;
        this.type = type;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return loc;
    }

    public WarpType getType() {
        return type;
    }

    public WarpPointPlayer getOwner() {
        return owner;
    }

    public void teleportPlayer(Player p) {
        //TODO: Do complex teleport shit like HomeSpawn
    }

}
