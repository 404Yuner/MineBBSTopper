package com.mythmc.api;


import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.tools.utils.CommandUtil;
import com.mythmc.tools.utils.TimeUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class GlobalAPI {

    /**
     * 将时间戳转换为格式化的日期时间字符串
     *
     * @param timestamp 时间戳 单位秒
     * @return 格式化的日期时间字符串
     */
    public static String convertTimestamp(long timestamp) {
        return TimeUtil.convertTimestamp(timestamp);
    }

    /**
     * 将日期时间字符串转换为时间戳
     *
     * @param dateTimeStr 日期时间字符串
     * @return 时间戳
     */
    public static long convertDataTime(String dateTimeStr) {
        return TimeUtil.convertDataTime(dateTimeStr);
    }

    /**
     * 检查当前时间是否在配置的时间跨度内
     *
     * @return 如果在时间跨度内返回 true，否则返回 false
     */
    public static boolean isCurrentHourWithinSpan() {
        return TimeUtil.isCurrentHourWithinSpan();
    }

    /**
     * 获取全局信息
     *
     * @return 全局信息对象
     */
    public static GlobalInfo getGlobalInfo() {
        return TargetManager.getGlobalInfo();
    }

    /**
     * 刷新全局信息
     */
    public static void refreshGlobalInfo() {
        TargetManager.refreshGlobalInfo();
    }

    /**
     * 执行内置动作
     *
     * @param player 玩家对象
     * @param commands 动作指令集
     */
    public static void executeCommands(Player player, List<String> commands) {
        CommandUtil.executeCommands(player,commands);
    }
}

