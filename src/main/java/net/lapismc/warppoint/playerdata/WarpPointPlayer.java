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

import net.lapismc.lapiscore.LocationUtils;
import net.lapismc.warppoint.WarpPoint;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An object to store and access warps that a player has set
 */

public class WarpPointPlayer {

    private WarpPoint plugin;
    private UUID uuid;
    private YamlConfiguration config;
    private ArrayList<Warp> warps = new ArrayList<>();

    public WarpPointPlayer(WarpPoint plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        File f = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + uuid.toString() + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(f);
        loadWarps();
    }

    /**
     * Get the UUID of the player this object represents
     *
     * @return Returns the UUID for this player
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Add a warp to this player
     * This also creates the required config values for this warp to persist
     *
     * @param warp The warp you wish to add, this player should own the warp
     */
    public void addWarp(Warp warp) {
        String path = "Warps." + warp.getName() + "_" + warp.getType().toString();
        config.set(path, new LocationUtils().parseLocationToString(warp.getLocation()));
        saveConfig();
        warps.add(warp);
        if (warp.getType().equals(WarpType.Public))
            plugin.warpManager.addPublicWarp(warp);
    }

    /**
     * Remove a warp that this player currently has
     * This also removes the required config values for the deletion to persist
     *
     * @param warp The warp you wish to remove
     */
    public void removeWarp(Warp warp) {
        String path = "Warps." + warp.getName() + "_" + warp.getType().toString();
        config.set(path, null);
        saveConfig();
        warps.remove(warp);
        if (warp.getType().equals(WarpType.Public))
            plugin.warpManager.removePublicWarp(warp);
    }

    /**
     * Get all warps owned by this player
     *
     * @return Returns all warps this player owns, regardless of type
     */
    public List<Warp> getAllWarps() {
        return warps;
    }

    /**
     * Get all public warps owned by this player
     *
     * @return Returns all public warps this player owns, not all public warps
     */
    public List<Warp> getPublicWarps() {
        List<Warp> list = new ArrayList<>();
        for (Warp w : warps) {
            if (w.getType().equals(WarpType.Public))
                list.add(w);
        }
        return list;
    }

    /**
     * Get all this players private warps
     *
     * @return Returns all private warps this player owns
     */
    public List<Warp> getPrivateWarps() {
        List<Warp> list = new ArrayList<>();
        for (Warp w : warps) {
            if (w.getType().equals(WarpType.Private))
                list.add(w);
        }
        return list;
    }

    /**
     * Get all faction warps that this player owns
     *
     * @return Returns all faction warps this player owns, not all faction warps
     */
    public List<Warp> getFactionWarps() {
        List<Warp> list = new ArrayList<>();
        for (Warp w : warps) {
            if (w.getType().equals(WarpType.Faction))
                list.add(w);
        }
        return list;
    }

    /*
    Utility Methods
     */

    private void loadWarps() {
        //Get the warps section of the config
        ConfigurationSection warpsSection = config.getConfigurationSection("Warps");
        //Loop over each warp
        for (String warpString : warpsSection.getKeys(false)) {
            //Parse the location of the warp
            Location loc = new LocationUtils().parseStringToLocation(config.getString("Warps." + warpString));
            //Split the key to get the Name and Type
            String[] warpData = warpString.split("_");
            String name = warpData[0];
            WarpType type = WarpType.valueOf(warpData[1]);
            //Create the warp and register it
            Warp warp = new Warp(name, loc, type, this);
            warps.add(warp);
            //Add it to the public and faction systems where applicable
            if (type.equals(WarpType.Public))
                plugin.warpManager.addPublicWarp(warp);
            if (type.equals(WarpType.Faction))
                plugin.warpManager.addFactionWarp(warp);
        }
    }

    private void saveConfig() {
        File f = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + uuid.toString() + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
