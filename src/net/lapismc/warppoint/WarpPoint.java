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

package net.lapismc.warppoint;

import net.lapismc.warppoint.api.WarpPointAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class WarpPoint extends JavaPlugin {

    final Logger logger = getLogger();
    public WarpPointConfigurations WPConfigs;
    public WarpPointWarps WPWarps;
    public WarpPointFactions WPFactions;
    public WarpPointPerms WPPerms;
    public boolean factions;
    public LapisUpdater lapisUpdater;
    public String PrimaryColor = "&6";
    public String SecondaryColor = "&c";

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            factions = true;
            WPFactions = new WarpPointFactions(this);
        } else {
            factions = false;
        }
        lapisUpdater = new LapisUpdater(this, "WarpPoint", "LapisPlugins", "WarpPoint", "master");
        update();
        new Metrics(this);
        new WarpPointFileWatcher(this);
        WPWarps = new WarpPointWarps(this);
        WPConfigs = new WarpPointConfigurations(this);
        WPConfigs.generateConfigurations();
        WPConfigs.loadConfigurations();
        new WarpPointCommands(this);
        new WarpPointListeners(this);
        new WarpPointAPI(this);
        WPPerms = new WarpPointPerms(this);
        WPPerms.loadPermissions();
        logger.info("WarpPoint v." + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        WPConfigs.saveConfigurations();
        logger.info("WarpPoint Disabled");
    }

    private void update() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (getConfig().getBoolean("DownloadUpdates")) {
                lapisUpdater.downloadUpdate();
            } else {
                if (lapisUpdater.checkUpdate()) {
                    logger.info("Update for WarpPoint available, you can install it with /warppoint update");
                } else {
                    logger.info("No updates found for WarpPoint");
                }
            }
        });
    }

    public enum WarpType {
        Private, Public, Faction;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
