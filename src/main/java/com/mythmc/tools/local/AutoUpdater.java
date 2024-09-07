package com.mythmc.tools.local;

import com.mythmc.MineBBSTopper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AutoUpdater {
    private final MineBBSTopper plugin;

    public AutoUpdater(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    public void load() {
        renameOldFolder();
        handleConfigFiles();
    }

    private void renameOldFolder() {
        String oldPath = plugin.getDataFolder().getAbsolutePath() + "1"; // 原有文件夹路径
        File oldDir = new File(oldPath);

        if (!oldDir.exists()) {
          //  plugin.logger("§5更新 §8| §a旧文件夹不存在！");
            return;
        }

        if (!oldDir.isDirectory()) {
           // plugin.logger("§5更新 §8| §a旧路径不是一个目录！");
            return;
        }

        String newPath = plugin.getDataFolder().getAbsolutePath(); // 新的文件夹路径
        File newDir = new File(newPath);

        // 尝试重命名文件夹
        boolean success = oldDir.renameTo(newDir);

        if (success) {
            plugin.logger("§5更新 §8| §c已将插件版本v3.1生成的错误文件夹重命名成功！");
        }
//        } else {
//            plugin.logger("§5更新 §8| §c重命名旧文件夹失败！");
//        }
    }

    private void handleConfigFiles() {

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File oldConfigFile = new File(plugin.getDataFolder(), "config-old.yml");
        File oldBackupFile = new File(plugin.getDataFolder(), "config-old-backup.yml");

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String configVersion = config.getString("Version");

        // 这里为了适应低版本插件用户
        if (!"2.0".equals(configVersion)) {
            // 备份旧配置文件
            if (configFile.exists()) {
                try {
                    // 如果 config-old.yml 已经存在，则重命名为 config-old-backup.yml
                    if (oldConfigFile.exists()) {
                        Files.move(oldConfigFile.toPath(), oldBackupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        plugin.logger("§5更新 §8| §a旧备份文件已存在，已重命名为 config-old-backup.yml");
                    }
                    // 备份当前的 config.yml 文件
                    Files.move(configFile.toPath(), oldConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.logger("§5更新 §8| §a旧配置文件已备份为 config-old.yml");
                } catch (IOException e) {
                    plugin.logger("§5更新 §8| §c无法备份旧配置文件: " + e.getMessage());
                }
            }
            // 生成新的默认配置文件
            plugin.saveDefaultConfig();
            plugin.logger("§5更新 §8| §c已生成新的默认配置文件，请查看配置！");
        } else {
            plugin.saveDefaultConfig();
        }
    }
}