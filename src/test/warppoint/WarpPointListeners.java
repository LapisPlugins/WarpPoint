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

package test.warppoint;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

class WarpPointListeners implements Listener {

    private final WarpPoint plugin;

    WarpPointListeners(WarpPoint plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void playerJoinEvent(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        YamlConfiguration warps;
        File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" +
                File.separator + p.getUniqueId() + ".yml");
        if (!f.exists()) {
            plugin.config.generateNewPlayerData(f, p);
        }
        warps = plugin.config.getPlayerConfig(p.getUniqueId());
        Date date = new Date();
        warps.set("OnlineSince", date.getTime());
        plugin.config.reloadPlayerConfig(p.getUniqueId(), warps);
        if (!warps.getString("UUID").equals(p.getUniqueId().toString())) {
            warps.set("UUID", p.getUniqueId().toString());
            plugin.config.reloadPlayerConfig(p.getUniqueId(), warps);
        }
        if (!warps.getString("UserName").equals(p.getName())) {
            warps.set("UserName", p.getName());
            plugin.config.reloadPlayerConfig(p.getUniqueId(), warps);
        }
        Integer priority = 0;
        Integer lowestPriority = null;
        Permission lowestPermission = null;
        Permission currentPerm = null;
        for (Permission perm : plugin.perms.pluginPerms.keySet()) {
            HashMap<WarpPointPerms.Perm, Integer> map = plugin.perms.pluginPerms.get(perm);
            if (lowestPriority == null || map.get(WarpPointPerms.Perm.Priority) < lowestPriority) {
                lowestPermission = perm;
                lowestPriority = map.get(WarpPointPerms.Perm.Priority);
            }
            if (p.hasPermission(perm)) {
                if (map.get(WarpPointPerms.Perm.Priority) > priority) {
                    priority = map.get(WarpPointPerms.Perm.Priority);
                    currentPerm = perm;
                }
            }
        }
        if (currentPerm != null) {
            plugin.logger.info("Player " + p.getName() + " has been assigned permission " + currentPerm.getName());
            plugin.perms.setPerms(p.getUniqueId(), currentPerm);
        } else {
            assert lowestPermission != null;
            plugin.logger.info("Player " + p.getName() + " has no permissions so they have been given "
                    + lowestPermission.getName() + " by default");
            plugin.perms.setPerms(p.getUniqueId(), lowestPermission);
            currentPerm = lowestPermission;
        }
        if (plugin.perms.pluginPerms.get(currentPerm).get(WarpPointPerms.Perm.Admin) == 1) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (plugin.lapisUpdater.checkUpdate()) {
                    if (!plugin.getConfig().getBoolean("DownloadUpdates")) {
                        p.sendMessage(plugin.config.getMessage("Update.Available"));
                    }
                }
            });
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Date date = new Date();
        YamlConfiguration warps = plugin.config.getPlayerConfig(p.getUniqueId());
        warps.set("OfflineSince", date.getTime());
        plugin.config.reloadPlayerConfig(p.getUniqueId(), warps);
        plugin.config.unloadPlayerData(p.getUniqueId());
    }

}
