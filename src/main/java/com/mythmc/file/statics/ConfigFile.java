package com.mythmc.file.statics;

import com.mythmc.MineBBSTopper;
import com.mythmc.tools.utils.ColorUtil;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
public class ConfigFile {
    public static String Version,DataType,MySQLHost,MySQLDatabase,MySQLUser,MySQLPassword,url,reconnect,claimSpan,techUrl,proxyIP;
    public static boolean UpdateCheck,MySQLUseSSL,enableLimit,enableOffdayReward,isExtraRewards,isProxyMode,sendRemindMessage,isOldFormat;
    public static int MySQLPort,cooldown,claimTime,rankRefreshInterval,limitTimes,proxyPort,rankPlayer;
    public static double openInterval,clickInterval;
    public static List<String> normalRewardCommands,offdayRewardCommands,offdays;

    public void load() {
        FileConfiguration config = MineBBSTopper.INSTANCE.getConfig();
        Version = config.getString("Version", "1.0");
        UpdateCheck = config.getBoolean("UpdateCheck", true);
        // 数据库配置
        DataType = config.getString("Database.type", "SQLite");
        MySQLHost = config.getString("Database.MySQL.host");
        MySQLPort = config.getInt("Database.MySQL.port");
        MySQLDatabase = config.getString("Database.MySQL.database");
        MySQLUser = config.getString("Database.MySQL.user");
        MySQLPassword = config.getString("Database.MySQL.password");
        MySQLUseSSL = config.getBoolean("Database.MySQL.useSSL");

        // 具体设置项目
        url = config.getString("Setting.serverUrl");
        reconnect = config.getString("Setting.reconnect");
        cooldown = config.getInt("Setting.cooldown");
        claimTime = config.getInt("Setting.claimTime");
        claimSpan = ColorUtil.colorize(config.getString("Setting.claimSpan"));
        openInterval = config.getDouble("Setting.openInterval");
        clickInterval = config.getDouble("Setting.clickInterval");
        techUrl = config.getString("Setting.techUrl");
        rankRefreshInterval = config.getInt("Setting.rankRefreshInterval");
        sendRemindMessage = config.getBoolean("Setting.sendRemindMessage", true);
       // isOldFormat = config.getBoolean("Setting.isOldFormat",false);
        rankPlayer = config.getInt("Setting.rankPlayer", 10);
        // 奖励的配置
        normalRewardCommands = (List<String>) config.getList("Rewards.normal.commands");
        offdayRewardCommands = (List<String>) config.getList("Rewards.offday.commands");
        offdays = (List<String>) config.getList("Rewards.offday.days");

        enableLimit = config.getBoolean("Rewards.limit.enable", false);
        limitTimes = config.getInt("Rewards.limit.times");

        enableOffdayReward = config.getBoolean("Rewards.offday.enable", false);
        isExtraRewards = config.getBoolean("Rewards.offday.extra", false);

        // 代理的配置
        isProxyMode = config.getBoolean("Setting.proxy.enable",false);
        proxyIP = config.getString("Setting.proxy.ip", "127.0.0.1");
        proxyPort = config.getInt("Setting.proxy.port", 10809);
    }



}
