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

package net.lapismc.warppoint.api;

import net.lapismc.warppoint.playerdata.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class WarpDeleteEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Warp warp;
    private String cancelReason;
    private boolean cancelled;

    public WarpDeleteEvent(Warp warp) {
        this.warp = warp;
    }

    //required event methods

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Deprecated
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    //My methods

    public void setCancelled(boolean cancel, String reason) {
        cancelled = cancel;
        cancelReason = reason;
    }

    public String getCancelReason() {
        if (cancelled) {
            return cancelReason;
        } else {
            return null;
        }
    }

    public Warp getWarp() {
        return warp;
    }

    public Player getPlayer() {
        return warp.getOwner().getPlayer();
    }

}
