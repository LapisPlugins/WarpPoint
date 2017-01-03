package net.lapismc.warppoint;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarpPointListeners implements Listener {

    WarpPoint plugin;

    protected WarpPointListeners(WarpPoint plugin) {
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
            try {
                f.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            warps = YamlConfiguration.loadConfiguration(f);
            warps.set("UUID", p.getUniqueId().toString());
            warps.set("UserName", p.getName());
            List<String> sl = new ArrayList<>();
            warps.set("Warps.list", sl);
            plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
        }
        warps = YamlConfiguration.loadConfiguration(f);
        if (!warps.getString("UUID").equals(p.getUniqueId().toString())) {
            warps.set("UUID", p.getUniqueId().toString());
            plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
        }
        if (!warps.getString("UserName").equals(p.getName())) {
            warps.set("UserName", p.getName());
            plugin.WPConfigs.reloadPlayerConfig(p.getUniqueId(), warps);
        }
        Integer priority = 0;
        Integer lowestPriority = null;
        Permission lowestPermission = null;
        Permission currentPerm = null;
        for (Permission perm : plugin.WPPerms.pluginPerms.keySet()) {
            HashMap<WarpPointPerms.Perms, Integer> map = plugin.WPPerms.pluginPerms.get(perm);
            if (lowestPriority == null || map.get(WarpPointPerms.Perms.Priority) < lowestPriority) {
                lowestPermission = perm;
                lowestPriority = map.get(WarpPointPerms.Perms.Priority);
            }
            if (p.hasPermission(perm)) {

                if (map.get(WarpPointPerms.Perms.Priority) > priority) {
                    priority = map.get(WarpPointPerms.Perms.Priority);
                    currentPerm = perm;
                }
            }
        }
        if (currentPerm != null) {
            plugin.logger.info("Player " + p.getName() + " has been assigned permission " + currentPerm.getName());
            plugin.WPPerms.setPerms(p.getUniqueId(), currentPerm);
        } else {
            plugin.logger.info("Player " + p.getName() + " has no permissions so they have been given "
                    + lowestPermission.getName() + " by default");
            plugin.WPPerms.setPerms(p.getUniqueId(), lowestPermission);
        }
        if (plugin.WPPerms.isPermitted(p.getUniqueId(), WarpPointPerms.Perms.Admin)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if (plugin.lapisUpdater.checkUpdate("WarpPoint")) {
                        if (!plugin.getConfig().getBoolean("DownloadUpdates")) {
                            p.sendMessage(plugin.WPConfigs.getColoredMessage("Update.Available"));
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.WPConfigs.unloadPlayerData(p.getUniqueId());
    }

}
