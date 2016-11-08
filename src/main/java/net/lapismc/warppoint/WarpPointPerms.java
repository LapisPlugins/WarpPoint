package net.lapismc.warppoint;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;


public class WarpPointPerms {

    private WarpPoint plugin;


    private HashMap<UUID, HashMap<Perm, Boolean>> Perms = new HashMap<>();

    protected WarpPointPerms(WarpPoint p) {
        p = plugin;
    }

    public void setPerms(UUID uuid, HashMap<Perm, Boolean> map) {
        Perms.put(uuid, map);
    }

    public boolean isPermitted(Player p, Perm perm) {
        HashMap<Perm, Boolean> map = Perms.get(p.getUniqueId());
        return map.get(perm);
    }

    public enum Perm {
        Private, PublicSet, PublicMove, PublicTele, FactionSet, FactionMove, FactionTele;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
