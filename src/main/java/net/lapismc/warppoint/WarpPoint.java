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

package net.lapismc.warppoint;

import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.warppoint.playerdata.WarpPointPlayer;
import net.lapismc.warppoint.playerdata.warpmanager.WarpManager;

import java.util.UUID;

public class WarpPoint extends LapisCorePlugin {

    public WarpManager warpManager;

    @Override
    public void onEnable() {
        warpManager = new WarpManager(this);
        getLogger().info(getName() + " v." + getDescription().getVersion() + " has been enabled!");
    }


    public WarpPointPlayer getPlayer(UUID uuid) {
        return new WarpPointPlayer(this, uuid);
    }

}
