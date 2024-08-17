package com.mythmc.api;

import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.database.DatabaseManager;
import org.bukkit.entity.Player;

public class PlayerAPI {

    /**
     * 获取玩家信息对象，如果玩家信息不存在，则创建新的 PlayerInfo 实例。
     *
     * @param player 要获取信息的玩家
     * @return 玩家信息对象
     */
    public static PlayerInfo getPlayerInfo(Player player) {
        PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);
        if (playerInfo == null) {
            // 如果 PlayerInfo 对象不存在，则创建新的对象
            playerInfo = TargetManager.playerInfoCache.computeIfAbsent(player.getName(), info -> new PlayerInfo(player));
        }
        return playerInfo;
    }

    /**
     * 获取玩家的累计奖励金额。
     *
     * @param player 要查询的玩家
     * @return 累计奖励金额
     */
    public static int getAmount(Player player) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        return playerInfo.getAmount();
    }

    /**
     * 设置玩家的累计奖励金额。
     *
     * @param player 要更新的玩家
     * @param amount 要设置的金额
     */
    public static void setAmount(Player player, int amount) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        playerInfo.addAmount(amount);
        // 这里可以添加更新数据库的逻辑，如果需要的话
    }

    /**
     * 获取玩家的上次记录时间。
     *
     * @param player 要查询的玩家
     * @return 上次记录时间
     */
    public static String getLastTime(Player player) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        return playerInfo.getLastTimeStr();
    }

    /**
     * 设置玩家的上次记录时间。
     *
     * @param player    要更新的玩家
     * @param lastTime  要设置的时间
     */
    public static void setLastTime(Player player, String lastTime) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        playerInfo.setLastTimeStr(lastTime);
        // 这里可以添加更新数据库的逻辑，如果需要的话
    }

    /**
     * 获取玩家的奖励数据。
     *
     * @param player 要查询的玩家
     * @return 奖励数据数组
     */
    public static String[] getRewardData(Player player) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        return playerInfo.getRewardData();
    }

    /**
     * 设置玩家的奖励数据。
     *
     * @param player 要更新的玩家
     * @param signal 奖励信号
     * @param index  奖励数据的索引
     */
    public static void setRewardData(Player player, String signal, int index) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        playerInfo.setRewardData(signal, index);
        // 这里可以添加更新数据库的逻辑，如果需要的话
    }

    /**
     * 获取玩家的今天奖励金额。
     *
     * @param player 要查询的玩家
     * @return 今天的奖励金额
     */
    public static int getTodayAmount(Player player) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        return playerInfo.getTodayAmount();
    }

    /**
     * 设置玩家的今天奖励金额。
     *
     * @param player 要更新的玩家
     * @param amount 今天的奖励金额
     */
    public static void setTodayAmount(Player player, int amount) {
        PlayerInfo playerInfo = getPlayerInfo(player);
        playerInfo.addTodayAmount(amount);
        // 这里可以添加更新数据库的逻辑，如果需要的话
    }
}