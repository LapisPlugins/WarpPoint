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

package test.warppoint.playerdata;

import net.lapismc.lapiscore.LapisPermission;

public enum Permission {

    //Admin, Private, PublicWarps, PublicMove, PublicTele, FactionWarps, FactionMove, FactionTele;

    Admin(new Admin()), PrivateWarps(new PrivateWarps()), PublicWarps(new Spawn()), PublicMove(new SetSpawn()),
    PublicTele(new PublicTele()), FactionWarps(new CanUpdate()), FactionMove(new CanReload()), FactionTele(new CanViewPlayerStats());

    private final LapisPermission permission;

    Permission(LapisPermission permission) {
        this.permission = permission;
    }

    public LapisPermission getPermission() {
        return this.permission;
    }

    private static class Admin extends LapisPermission {
        //If enabled players with this permission will be able to delete all warps and access the admin panel
        Admin() {
            super("Admin");
        }
    }

    private static class PrivateWarps extends LapisPermission {
        //How many private warps can they set
        PrivateWarps() {
            super("PrivateWarps");
        }
    }

    private static class Spawn extends LapisPermission {
        //Allows the player to teleport to spawn
        Spawn() {
            super("Spawn");
        }
    }

    private static class SetSpawn extends LapisPermission {
        //Allows the player to set spawn
        SetSpawn() {
            super("SetSpawn");
        }
    }

    private static class PublicTele extends LapisPermission {
        //Is this player allowed to teleport to public warps
        PublicTele() {
            super("PublicTele");
        }
    }

    private static class CanUpdate extends LapisPermission {
        //Allows the player to update the plugin
        CanUpdate() {
            super("CanUpdate");
        }
    }

    private static class CanReload extends LapisPermission {
        //Allows the player to reload the plugin
        CanReload() {
            super("CanReload");
        }
    }

    private static class CanViewPlayerStats extends LapisPermission {
        //Allows the player to view other players statistics
        CanViewPlayerStats() {
            super("CanViewPlayerStats");
        }
    }

}
