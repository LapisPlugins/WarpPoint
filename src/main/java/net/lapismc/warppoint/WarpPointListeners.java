package net.lapismc.warppoint;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;

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
        if (f.exists()) {

        }
    }

}
