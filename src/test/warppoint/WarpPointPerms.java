/*
 * Copyright  2018 Benjamin Martin
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

package test.warppoint;

import com.massivecraft.factions.Perm;
import net.lapismc.lapiscore.LapisCorePermissions;
import org.bukkit.permissions.Permission;

import java.util.HashMap;


public class WarpPointPerms extends LapisCorePermissions {

    final HashMap<Permission, HashMap<Perm, Integer>> pluginPerms = new HashMap<>();
    private final WarpPoint plugin;

    WarpPointPerms(WarpPoint p) {
        super(p);
        plugin = p;
    }



    //warps.set("Permission", p.getName());
    //                    plugin.config.reloadPlayerConfig(uuid, warps);
    //                    playerPerms.put(uuid, p);


    //String permName = warps.getString("Permission");
    //            for (Permission perm : pluginPerms.keySet()) {
    //                if (perm.getName().equals(permName)) {
    //                    return perm;
    //                }
    //            }
    //            return null;

}
