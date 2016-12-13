package net.lapismc.warppoint;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
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
    }

    @EventHandler
    public void playerJoinEvent(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        File f = new File(plugin.getDataFolder().getAbsolutePath() + "PlayerData" +
                File.separator + p.getUniqueId() + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            yaml.set("UUID", p.getUniqueId());
            yaml.set("UserName", p.getName());
            List<String> sl = new ArrayList<String>();
            yaml.set("Warps.list", sl);
        }
        Integer priority = 0;
        Permission currentPerm = null;
        for (Permission perm : plugin.WPPerms.pluginPerms.keySet()) {
            if (p.hasPermission(perm)) {
                HashMap<WarpPointPerms.Perms, Integer> map = plugin.WPPerms.pluginPerms.get(perm);
                if (map.get(WarpPointPerms.Perms.Priority) > priority) {
                    priority = map.get(WarpPointPerms.Perms.Priority);
                    currentPerm = perm;
                }
            }
        }
        if (currentPerm != null) {
            plugin.logger.info("Player " + p.getName() + " has been assigned permission " + currentPerm.getName());
            plugin.WPPerms.setPerms(p.getUniqueId(), currentPerm);
        }
    }

}
