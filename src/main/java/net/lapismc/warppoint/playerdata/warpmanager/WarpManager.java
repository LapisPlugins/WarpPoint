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

package net.lapismc.warppoint.playerdata.warpmanager;

import net.lapismc.warppoint.WarpPoint;
import net.lapismc.warppoint.playerdata.Warp;
import net.lapismc.warppoint.playerdata.WarpType;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A class for managing Public warps
 */
public class WarpManager {

    private WarpPoint plugin;
    private FactionWarpManager factionWarpManager = null;
    private List<Warp> publicWarps = new ArrayList<>();

    public WarpManager(WarpPoint plugin) {
        this.plugin = plugin;
        loadWarps();
        if (Bukkit.getPluginManager().isPluginEnabled("Factions"))
            factionWarpManager = new FactionWarpManager();
    }

    public void addPublicWarp(Warp warp) {
        if (warp.getType().equals(WarpType.Public))
            publicWarps.add(warp);
    }

    public void addFactionWarp(Warp warp) {
        if (factionWarpManager != null)
            factionWarpManager.addWarp(warp);
    }

    public void removePublicWarp(Warp warp) {
        publicWarps.remove(warp);
    }

    public List<Warp> getPublicWarps() {
        return publicWarps;
    }

    private void loadWarps() {
        File playerDataDirectory = new File(plugin.getDataFolder(), "PlayerData");
        if (!playerDataDirectory.exists())
            playerDataDirectory.mkdir();
        for (File f : Objects.requireNonNull(playerDataDirectory.listFiles())) {
            plugin.getPlayer(UUID.fromString(f.getName().replace(".yml", "")));
        }
    }
}