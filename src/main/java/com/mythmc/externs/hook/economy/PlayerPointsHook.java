package com.mythmc.externs.hook.economy;

import com.mythmc.file.statics.LangFile;
import com.mythmc.tools.utils.MessageUtil;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlayerPointsHook {
    private static PlayerPointsAPI playerPointsAPI;
    private static final String ERROR_PREFIX = "\n" + LangFile.prefix + "§c";

    public static boolean setupPlayerPointsAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            playerPointsAPI = PlayerPoints.getInstance().getAPI();
            return true;
        }
        return false;
    }

    public static boolean hasValidPoints() {
        return (playerPointsAPI != null);
    }

    public static void givePoints(Player player, String amount) {
        if (!hasValidPoints()) {
            sendErrorMessage(player, "点券系统暂未开启，请联系管理员！待发放点券金额为: " + amount);
            return;
        }

        try {
            int points = Integer.parseInt(amount);
            if (points < 0) {
                sendErrorMessage(player, "点券奖励配置错误，错误的点券为: " + amount);
            } else {
                playerPointsAPI.give(player.getUniqueId(), points);
                MessageUtil.sendMessage(player, "§a您已获得 " + points + " 点券");
            }
        } catch (NumberFormatException e) {
            sendErrorMessage(player, "点券奖励配置错误，错误的点券为: " + amount);
        }
    }

    private static void sendErrorMessage(Player player, String message) {
        player.sendMessage(ERROR_PREFIX + message + "\n请将此消息截图后发送给管理员确认！");
    }
}