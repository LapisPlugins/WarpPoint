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

package net.lapismc.warppoint.playerdata.warpmanager;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import net.lapismc.warppoint.playerdata.Warp;
import net.lapismc.warppoint.playerdata.WarpType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A class for managing faction warps
 */
public class FactionWarpManager implements Listener {

    private HashMap<Faction, List<Warp>> factionWarps = new HashMap<>();

    //TODO: Deal with deletion of warps

    void addWarp(Warp warp) {
        if (!warp.getType().equals(WarpType.Faction))
            return;
        Faction ownersFaction = getFaction(warp.getOwner().getUniqueId());
        List<Warp> currentFactionWarps = factionWarps.get(ownersFaction) != null ? factionWarps.get(ownersFaction) : new ArrayList<>();
        currentFactionWarps.add(warp);
        factionWarps.put(ownersFaction, currentFactionWarps);
    }

    @EventHandler
    public void playerFactionChangeEvent(EventFactionsMembershipChange e) {
        //TODO: deal with moving the warp to the new faction when a player changes factions
    }

    /*
    Utility methods
     */

    private Faction getFaction(UUID uuid) {
        MPlayer fp = MPlayer.get(uuid);
        return fp.getFaction();
    }
}
