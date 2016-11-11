package net.lapismc.warppoint;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

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
            HashMap<WarpPointPerms.Perms, Integer> map = plugin.WPPerms.getBlankPerms();
            for (WarpPointPerms.Perms perm : map.keySet()) {

            }
        }

    }

}
