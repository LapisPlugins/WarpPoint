package net.lapismc.warppoint;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class WarpPoint extends JavaPlugin {

    public WarpPointCommands WPCommands;
    public WarpPointListeners WPListeners;
    public WarpPointConfigurations WPConfigs;
    public WarpPointWarps WPWarps;
    public WarpPointFactions WPFactions;
    public WarpPointPerms WPPerms;
    public boolean factions;
    protected LapisUpdater lapisUpdater;
    Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        lapisUpdater = new LapisUpdater(this, "WarpPoint", "Dart2112", "WarpPoint", "master");
        if (lapisUpdater.checkUpdate("WarpPoint")) {
            logger.info("Update for WarpPoint available");
        } else {
            logger.info("No updates found for WarpPoint");
        }
        Metrics metrics = new Metrics(this);
        metrics.start();
        WPWarps = new WarpPointWarps(this);
        WPConfigs = new WarpPointConfigurations(this);
        WPConfigs.generateConfigurations();
        WPConfigs.loadConfigurations();
        WPCommands = new WarpPointCommands(this);
        WPListeners = new WarpPointListeners(this);
        WPPerms = new WarpPointPerms(this);
        WPPerms.loadPermissions();
        logger.info("WarpPoint v." + getDescription().getVersion() + " has been enabled");
        try {
            Class.forName("com.massivecraft.factions");
            factions = true;
            WPFactions = new WarpPointFactions(this);
        } catch (ClassNotFoundException e) {
            factions = false;
        }
    }

    @Override
    public void onDisable() {
        WPConfigs.saveConfigurations();
        logger.info("WarpPoint Disabled");
    }

    public enum WarpType {
        Private, Public, Faction;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
