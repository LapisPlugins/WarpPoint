package net.lapismc.warppoint;

import com.massivecraft.factions.entity.Faction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


public class WarpPointPerms {

    protected HashMap<Permission, HashMap<Perms, Integer>> pluginPerms = new HashMap<>();
    private WarpPoint plugin;
    private HashMap<UUID, Permission> playerPerms = new HashMap<>();

    protected WarpPointPerms(WarpPoint p) {
        plugin = p;
    }

    protected void loadPermissions() {
        if (plugin == null) {
            System.out.println("Plugin null");
            return;
        }
        ConfigurationSection permsSection = plugin.getConfig().getConfigurationSection("Permissions");
        Set<String> perms = permsSection.getKeys(false);
        for (String perm : perms) {
            String permName = perm.replace(",", ".");
            int Default = plugin.getConfig().getInt("Permissions." + perm + ".default");
            int priority = plugin.getConfig().getInt("Permissions." + perm + ".priority");
            int Admin = plugin.getConfig().getInt("Permissions." + perm + ".admin");
            int privateWarps = plugin.getConfig().getInt("Permissions." + perm + ".PrivateWarps");
            int publicTele = plugin.getConfig().getInt("Permissions." + perm + ".PublicTele");
            int publicWarps = plugin.getConfig().getInt("Permissions." + perm + ".PublicWarps");
            int publicMove = plugin.getConfig().getInt("Permissions." + perm + ".PublicMove");
            int factionTele = plugin.getConfig().getInt("Permissions." + perm + ".FactionTele");
            int factionWarps = plugin.getConfig().getInt("Permissions." + perm + ".FactionWarps");
            int factionMove = plugin.getConfig().getInt("Permissions." + perm + ".FactionMove");
            HashMap<Perms, Integer> permMap = new HashMap<>();
            permMap.put(Perms.Default, Default);
            permMap.put(Perms.Priority, priority);
            permMap.put(Perms.Admin, Admin);
            permMap.put(Perms.Private, privateWarps);
            permMap.put(Perms.PublicTele, publicTele);
            permMap.put(Perms.PublicWarps, publicWarps);
            permMap.put(Perms.PublicMove, publicMove);
            permMap.put(Perms.FactionTele, factionTele);
            permMap.put(Perms.FactionWarps, factionWarps);
            permMap.put(Perms.FactionMove, factionMove);
            PermissionDefault permissionDefault;
            switch (Default) {
                case 1:
                    permissionDefault = PermissionDefault.TRUE;
                    break;
                case 2:
                    permissionDefault = PermissionDefault.OP;
                    break;

                case 0:
                default:
                    permissionDefault = PermissionDefault.FALSE;
                    break;
            }
            Permission permission = new Permission(permName, permissionDefault);
            if (Bukkit.getPluginManager().getPermission(permName) == null) {
                Bukkit.getPluginManager().addPermission(permission);
            } else {
                plugin.logger.severe("Couldn't add permission " + permName + " as it already exists!");
            }
            pluginPerms.put(permission, permMap);
        }
    }

    public void setPerms(UUID uuid, Permission p) {
        playerPerms.put(uuid, p);
    }

    public Boolean isPermitted(Player p, Perms perm) {
        HashMap<Perms, Integer> permMap = pluginPerms.get(playerPerms.get(p.getUniqueId()));
        if (permMap == null || permMap.get(perm) == null) {
            return false;
        }
        if (perm.equals(Perms.FactionWarps)) {
            if (plugin.factions) {
                Integer i = 0;
                Faction f = plugin.WPFactions.getFaction(p);
                HashMap<String, UUID> map = plugin.WPFactions.factionWarps.get(f);
                for (UUID uuid : map.values()) {
                    if (uuid.equals(p.getUniqueId())) {
                        i++;
                    }
                }
                return i < permMap.get(perm);
            } else {
                return false;
            }
        } else if (perm.equals(Perms.PublicWarps)) {
            Integer i = 0;
            HashMap<String, UUID> map = plugin.WPWarps.publicWarps;
            for (UUID uuid : map.values()) {
                if (uuid.equals(p.getUniqueId())) {
                    i++;
                }
            }
            return i < permMap.get(perm);
        } else if (perm.equals(Perms.Private)) {
            ArrayList<String> list = plugin.WPWarps.privateWarps;
            Integer i = 0;
            for (String s : list) {
                String[] sArray = s.split(":");
                if (sArray[1].equals(p.getUniqueId().toString())) {
                    i++;
                }
            }
            return i < permMap.get(perm);
        } else {
            return permMap.get(perm) == 1;
        }
    }

    public enum Perms {
        Default, Priority, Admin, Private, PublicWarps, PublicMove, PublicTele, FactionWarps, FactionMove, FactionTele;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
