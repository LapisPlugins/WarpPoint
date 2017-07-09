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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import static java.nio.file.StandardWatchEventKinds.*;

class WarpPointFileWatcher {

    private WarpPoint plugin;

    WarpPointFileWatcher(WarpPoint p) {
        plugin = p;
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                watcher();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void watcher() throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(plugin.getDataFolder().getAbsolutePath());
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        plugin.logger.info("WarpPoint file watcher started!");
        WatchKey key = watcher.take();
        while (key != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                File f = fileName.toFile();
                if (kind == ENTRY_CREATE) {
                    if (f.getPath().contains("PlayerData") && f.getName().endsWith(".yml")) {
                        checkPlayerData(f);
                    }
                } else if (kind == ENTRY_DELETE) {
                    if (f.getPath().contains("PlayerData") && f.getName().endsWith(".yml")) {
                        String uuidS = f.getName().replace(".yml", "");
                        UUID uuid = UUID.fromString(uuidS);
                        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                        if (op.isOnline()) {
                            Player p = op.getPlayer();
                            plugin.WPConfigs.generateNewPlayerData(f, p);
                            p.sendMessage("Your WarpPoint player data file has been deleted, a new one has been generated");
                        }
                    } else if (f.getName().endsWith(".yml")) {
                        String name = f.getName().replace(".yml", "");
                        switch (name) {
                            case "config":
                                plugin.saveDefaultConfig();
                                plugin.reloadConfig();
                                break;
                            case "Messages":
                                if (!f.createNewFile()) {
                                    plugin.logger.info("Failed to regenerate the deleted Messages.yml");
                                }
                                plugin.WPConfigs.generateMessages();
                                break;
                        }
                    }
                } else if (kind == ENTRY_MODIFY) {
                    if (f.getPath().contains("PlayerData") && f.getName().endsWith(".yml")) {
                        checkPlayerData(f);
                    } else if (f.getName().endsWith(".yml")) {
                        checkConfig(f);
                    }
                }
            }
            key.reset();
            key = watcher.take();
        }
        plugin.logger.severe("WarpPoint file watcher has stopped, please report any errors to dart2112 if this was not intended");
    }

    private void checkConfig(File f) {
        String name = f.getName().replace(".yml", "");
        switch (name) {
            case "config":
                plugin.reloadConfig();
                plugin.WPPerms.loadPermissions();
                plugin.logger.info("Changes made to WarpPoints config have been loaded");
                break;
            case "Messages":
                plugin.WPConfigs.reloadMessages(f);
                plugin.logger.info("Changes made to WarpPoints Messages.yml have been loaded");
                break;
        }
    }

    private void checkPlayerData(File f) {
        String uuidS = f.getName().replace(".yml", "");
        UUID uuid = UUID.fromString(uuidS);
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (op.isOnline()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            plugin.WPConfigs.reloadPlayerConfig(uuid, yaml);
        }

    }

}
