package com.mythmc.externs.hook.placeholders;

import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.impl.database.DatabaseManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderHook extends PlaceholderExpansion {

    // 默认构造函数
    public PlaceholderHook() {
    }

    // 替换列表中的占位符
    public static List<String> set(Player player, List<String> content) {
        return PlaceholderAPI.setPlaceholders(player, content);
    }

    // 替换字符串中的占位符
    public static String set(Player player, String content) {
        return PlaceholderAPI.setPlaceholders(player, content);
    }

    @Override
    public String getIdentifier() {
        return "minebbstopper";
    }

    @Override
    public String getAuthor() {
        return "404Yuner";
    }

    @Override
    public String getVersion() {
        return "1.7";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.startsWith("global_")) {
            return handleGlobalParam(params.substring(7).toLowerCase(), TargetManager.getGlobalInfo());
        } else if (params.startsWith("player_")) {
            return handlePlayerParam(params.substring(7).toLowerCase(), TargetManager.getPlayerInfo(player));
        } else {
            return "已更新占位符用法，请前往MINEBBS获取";
        }
    }

    private String handleGlobalParam(String params, GlobalInfo globalInfo) {

        switch (params) {
            case "cantop":
                return String.valueOf(globalInfo.getCooldownTimestamp() <= System.currentTimeMillis() / 1000);
            case "count":
                return String.valueOf(globalInfo.getAmount());
            case "cooldown_long":
                return String.valueOf(globalInfo.getCooldownTimestamp());
            case "cooldown_iso":
                return globalInfo.getCooldownTimeStr();
            case "rank":
                return "请提供具体的排名位次";
            default:
                if (params.startsWith("rank_")) {
                    // 提取动态部分的 rank
                    String rankNumber = params.substring(5); // "rank_1" -> "1"
                    return getRank(rankNumber);
                } else {
                    return "null";
                }
        }
    }

    private String handlePlayerParam(String params, PlayerInfo playerInfo) {
        // 处理固定的参数
        switch (params) {
            case "count":
                return String.valueOf(playerInfo.getAmount());
            case "lasttime":
                return String.valueOf(playerInfo.getLastTimeStr());
            case "todaycount":
                return String.valueOf(playerInfo.getTodayAmount());
            // 处理动态参数
            case "countother":
                return "请提供具体的玩家名";
            case "reward":
                return "请提供具体的奖励标签";
            default:
                // 处理以特定前缀开头的动态参数
                if (params.startsWith("countother_")) {
                    String playerParam = params.substring(12); // 提取 "_"+ 后面的部分
                    return String.valueOf(DatabaseManager.getDbManager().countPlayerRecords(playerParam));
                } else if (params.startsWith("reward_")) {
                    String rewardParam = params.substring(7); // 提取 "_"+ 后面的部分
                    return getRewardData(playerInfo, rewardParam);
                } else {
                    return "null";
                }
        }
    }

    private String getRewardData(PlayerInfo playerInfo, String parts) {
        try {
            String[] rewardData = playerInfo.getRewardData();
            int index = Integer.parseInt(parts);
            if (index >= 0 && index < rewardData.length) {
                if ("1".equals(rewardData[index - 1])) {
                    return "true";
                }
            }
            return "false";
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }
    private String getRank(String parts) {
        try {
            int position = Integer.parseInt(parts);
            List<String> cachedResult = PlaceholderCache.getCache();

            // 确保 cachedResult 不为空
            if (cachedResult == null) {
                return "---";
            }

            // 确保 position 在缓存列表的有效范围内
            return (position > 0 && position <= cachedResult.size()) ? cachedResult.get(position - 1) : "---";
        } catch (Exception e) {
            // 捕捉其他异常
            e.printStackTrace();
            return "---";
        }
    }

}
