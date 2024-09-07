package com.mythmc.impl.cache;

import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.impl.cache.target.PlayerInfo;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TargetManager {
    public static final Map<String, PlayerInfo> playerInfoCache = new HashMap<>();

    // 获取 PlayerInfo 实例
    // 如果缓存中没有，创建新的 PlayerInfo 实例并放入缓存
    public static PlayerInfo getPlayerInfo(Player player) {
        return playerInfoCache.computeIfAbsent(player.getName(), name -> new PlayerInfo(player));
    }

    // 移除缓存中的 PlayerInfo 实例（当玩家退出）
    public static void removePlayerInfo(String playerName) {
        playerInfoCache.remove(playerName);
    }

    // 清空缓存（每日重置当日顶贴次数的任务）
    public static void clearPlayerInfoCache() {
        playerInfoCache.clear();
    }

    // 缓存全局 GlobalInfo 实例
    private static final GlobalInfo globalInfoCache = new GlobalInfo();

    // 提供全局访问点
    public static GlobalInfo getGlobalInfo() {
        return globalInfoCache;
    }
    // 暂无用处
    public static void refreshGlobalInfo() {
        getGlobalInfo().loadInitialData();
    }
}
