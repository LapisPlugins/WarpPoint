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

package net.lapismc.warppoint.api;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.playerdata.WarpPointPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class WarpPointAPI {

    private static WarpPoint plugin;
    private static List<JavaPlugin> plugins;

    public WarpPointAPI(WarpPoint p) {
        plugin = p;
    }

    /**
     * @param plugin This should be the main class of your plugin
     */
    public WarpPointAPI(JavaPlugin plugin) {
        plugins.add(plugin);
    }

    /**
     * @param uuid the UUID of the player you wish to get data from
     * @return Returns the player data object for the player assosiated with that UUID, null if the player hasn't played before
     */
    public WarpPointPlayer getWarpPointPlayer(UUID uuid) {
        if (!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
            return null;
        }
        return new WarpPointPlayer(plugin, uuid);
    }

}
