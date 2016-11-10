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
    Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        WPCommands = new WarpPointCommands(this);
        WPListeners = new WarpPointListeners(this);
        WPWarps = new WarpPointWarps(this);
        WPPerms = new WarpPointPerms(this);
        WPConfigs = new WarpPointConfigurations(this);
        WPConfigs.generateConfigurations();
        WPConfigs.loadConfigurations();
        logger.info("WarpPoint v." + getDescription().getVersion() + " has been enabled");
        try {
            Class.forName("com.massivecraft.factions");
            factions = true;
        } catch (ClassNotFoundException e) {
            factions = false;
        }
        if (factions) {
            WPFactions = new WarpPointFactions(this);
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
