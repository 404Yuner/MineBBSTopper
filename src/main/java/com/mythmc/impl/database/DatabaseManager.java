package com.mythmc.impl.database;

import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.api.DbManager;
import com.mythmc.impl.database.mysql.MySQLConnection;
import com.mythmc.impl.database.mysql.MySQLManager;
import com.mythmc.impl.database.sqlite.SQLiteManager;
import com.mythmc.impl.database.yaml.YamlManager;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final MineBBSTopper plugin;


    public DatabaseManager(MineBBSTopper plugin) {
        this.plugin = plugin;
    }
    private static volatile DbManager dbManager = null;
    public static String dbPath = null;

    public void initialize(String type) {
        dbManager = null;
        // 如果 dbManager 为空
        if (dbManager == null) {
            // 对 DatabaseManager 类进行同步，以确保线程安全
            synchronized (DatabaseManager.class) {
                // 再次检查 dbManager 是否为空，以防多个线程同时进入此块
                if (dbManager == null) {
                    // 根据传入的 type 参数选择数据库管理器的类型
                    switch (type) {
                        case "mysql":
                            // 如果 type 为 mysql，创建 MySQLManager 实例
                            dbManager = new MySQLManager();
                            break;
                        case "sqlite":
                            // 如果 type 为 sqlite，创建 SQLiteManager 实例
                            dbManager = new SQLiteManager();
                            break;
                        case "yaml":
                            // 如果 type 为 yaml，创建 YamlManager 实例
                            dbManager = new YamlManager();
                            break;
                        default:
                            // 如果 type 不匹配任何已知类型，记录警告日志并禁用插件
                            plugin.logger("§c警告 §7| §c无法正确加载名为 §f" + ConfigFile.DataType + " §c的数据库");
                            plugin.getPluginLoader().disablePlugin(plugin);
                    }
                }
            }
        }
    }

    public void load() {
        plugin.logger("§6数据 §8| §a当前选择的数据库为: " + ConfigFile.DataType);
        String type = ConfigFile.DataType.toLowerCase();
        // 初始化数据库
        initialize(type);

        switch (type) {
            case "mysql":
                // mysql处理
                plugin.logger("§6数据 §8| §a正在尝试访问数据库");
                MySQLConnection.dataSource = new HikariDataSource(MySQLConnection.getHikariConfig());
                dbManager.createTable();
                break;
            case "sqlite":
                // sqlite处理
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists()) dataFolder.mkdirs();
                dbPath = dataFolder.getAbsolutePath() + File.separator + "database.db";
                // 检查数据库文件是否存在，如果不存在则重新创建数据库连接
                if (!new File(dbPath).exists()) plugin.logger("§6数据 §8| §e数据库文件不存在，重建数据库连接");
                dbManager.createTable();
                plugin.logger("§6数据 §8| §a成功创建数据库连接");
                break;
            case "yaml":
                // yaml处理
                dbManager.createTable();
                break;
            default:

        }
        writeOnlinePlayerRewardData();
    }
    private void writeOnlinePlayerRewardData() {
        // 适用于热加载插件时。写入数据，避免出现报错
        Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> !dbManager.playerRewardExists(name))
                .forEach(name -> dbManager.insertRewardData(name));
    }
    public void close() {
        // 关闭数据库链接
        String type = ConfigFile.DataType.toLowerCase();
        switch (type) {
            case "mysql":
                MySQLConnection.close();
                break;
            case "sqlite":
                SQLiteManager.close();
                break;
            case "yaml":

                break;
        }
        plugin.logger("§c卸载 §8| §6数据库连接已关闭: " + ConfigFile.DataType);
    }

    public static DbManager getDbManager() {
        return dbManager;
    }
}