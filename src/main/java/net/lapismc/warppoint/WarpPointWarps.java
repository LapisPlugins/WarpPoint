package net.lapismc.warppoint;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by benja on 18/09/2016.
 */
public class WarpPointWarps {

    public HashMap<String, UUID> publicWarps = new HashMap<>();
    public HashMap<String, UUID> privateWarps = new HashMap<>();
    WarpPoint plugin;

    protected WarpPointWarps(WarpPoint p) {
        plugin = p;
    }

    public void addPublicWarp(String s, UUID uuid) {
        publicWarps.put(s, uuid);
    }

    public void addPrivateWarp(String s, UUID uuid) {
        privateWarps.put(s, uuid);
    }

}
