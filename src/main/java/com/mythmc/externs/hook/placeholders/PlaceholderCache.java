package com.mythmc.externs.hook.placeholders;


import com.mythmc.file.statics.ConfigFile;
import com.mythmc.impl.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderCache {
    private static List<String> cache = new ArrayList<>();
    private static Long expiration;
    // 从数据库读取后存入缓存
    public static void putCache() {
        cache = DatabaseManager.getDbManager().getTopTenPlayers();
        expiration = System.currentTimeMillis() + ConfigFile.rankRefreshInterval * 1000L; // 过期时间配置
    }
    // 获取缓存
    public static List<String> getCache() {
        if (cache == null || expiration == null || System.currentTimeMillis() >= expiration) {
            putCache();
        }
        return cache;
    }

    // 清理缓存（卸载插件时）
    public static void clear() {
        cache.clear();
        expiration = null;
    }
}
