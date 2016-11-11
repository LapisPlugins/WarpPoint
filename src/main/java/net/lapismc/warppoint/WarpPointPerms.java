package net.lapismc.warppoint;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;


public class WarpPointPerms {

    private WarpPoint plugin;


    private HashMap<UUID, HashMap<Perms, Integer>> PlayerPerms = new HashMap<>();

    protected WarpPointPerms(WarpPoint p) {
        p = plugin;
    }

    public HashMap<Perms, Integer> getBlankPerms() {
        Perms[] permsArray = Perms.values();
        HashMap<Perms, Integer> map = new HashMap<>();
        int i = 0;
        while (i != 7) {
            Perms perm = permsArray[i];
            map.put(perm, 0);
            i++;
        }
        return map;
    }

    public void setPerms(UUID uuid, HashMap<Perms, Integer> map) {
        PlayerPerms.put(uuid, map);
    }

    public Boolean isPermitted(Player p, Perms perm) {
        HashMap<Perms, Integer> map = PlayerPerms.get(p.getUniqueId());
        return map.get(perm) == 5;
    }

    public enum Perms {
        Private, PublicSet, PublicMove, PublicTele, FactionSet, FactionMove, FactionTele;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
