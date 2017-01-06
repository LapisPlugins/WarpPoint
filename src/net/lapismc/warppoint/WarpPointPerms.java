/*
 * Copyright  2017 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapismc.warppoint;

import com.massivecraft.factions.entity.Faction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class WarpPointPerms {

    protected HashMap<Permission, HashMap<Perm, Integer>> pluginPerms = new HashMap<>();
    private WarpPoint plugin;
    private HashMap<UUID, Permission> playerPerms = new HashMap<>();

    protected WarpPointPerms(WarpPoint p) {
        plugin = p;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerPerms = new HashMap<>();
            }
        }, 20 * 60 * 5, 20 * 60 * 5);
    }

    protected void loadPermissions() {
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
            HashMap<Perm, Integer> permMap = new HashMap<>();
            permMap.put(Perm.Default, Default);
            permMap.put(Perm.Priority, priority);
            permMap.put(Perm.Admin, Admin);
            permMap.put(Perm.Private, privateWarps);
            permMap.put(Perm.PublicTele, publicTele);
            permMap.put(Perm.PublicWarps, publicWarps);
            permMap.put(Perm.PublicMove, publicMove);
            permMap.put(Perm.FactionTele, factionTele);
            permMap.put(Perm.FactionWarps, factionWarps);
            permMap.put(Perm.FactionMove, factionMove);
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

    public Permission getPlayerPermission(UUID uuid) {
        Permission p = null;
        Player player = Bukkit.getPlayer(uuid);
        if (!playerPerms.containsKey(uuid) || playerPerms.get(uuid).equals(null)) {
            Integer priority = 0;
            for (Permission perm : pluginPerms.keySet()) {
                if (player.hasPermission(perm) &&
                        (pluginPerms.get(perm).get(Perm.Priority) > priority)) {
                    p = perm;
                }
            }
            if (p == null) {
                return null;
            } else {
                playerPerms.put(uuid, p);
            }
        } else {
            p = playerPerms.get(uuid);
        }
        return p;
    }

    public Boolean isPermitted(UUID uuid, Perm perm) {
        HashMap<Perm, Integer> permMap;
        Permission p = getPlayerPermission(uuid);
        if (!pluginPerms.containsKey(p) || pluginPerms.get(p).equals(null)) {
            loadPermissions();
            permMap = pluginPerms.get(p);
        } else {
            permMap = pluginPerms.get(p);
        }
        if (perm.equals(Perm.FactionWarps)) {
            if (plugin.factions) {
                Integer i = 0;
                Faction f = plugin.WPFactions.getFaction(uuid);
                HashMap<String, UUID> map = plugin.WPFactions.factionWarps.get(f);
                for (UUID uuid0 : map.values()) {
                    if (uuid0.equals(uuid)) {
                        i++;
                    }
                }
                return i < permMap.get(perm);
            } else {
                return false;
            }
        } else if (perm.equals(Perm.PublicWarps)) {
            List<String> publicWarps = plugin.WPWarps.getOwnedPublicWarps(uuid);
            Integer i = publicWarps.size();
            return i < permMap.get(perm);
        } else if (perm.equals(Perm.Private)) {
            List<String> list = plugin.WPWarps.getPrivateWarps(uuid);
            Integer i = list.size();
            return i < permMap.get(perm);
        } else {
            return permMap.get(perm) == 1;
        }
    }

    public Integer getPermissionValue(UUID uuid, Perm p) {
        Permission perm = getPlayerPermission(uuid);
        return pluginPerms.get(perm).get(p);
    }

    public enum Perm {
        Default, Priority, Admin, Private, PublicWarps, PublicMove, PublicTele, FactionWarps, FactionMove, FactionTele;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
