package com.mythmc.commands.command.sub;

import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.file.statics.LangFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.impl.database.DatabaseManager;
import com.mythmc.tools.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Editor {
    public boolean handle(CommandSender sender, String[] args) {
        if (sender.hasPermission("minebbstopper.edit") || sender.getName().equalsIgnoreCase("CONSOLE")) {
            if (args.length < 4) {
                sender.sendMessage(LangFile.prefix + "使用方法：/mt edit [玩家名] [add/delete] 数量");
                return false;
            }

            String playerName = args[1];
            String action = args[2].toLowerCase();
            int count;

            try {
                count = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(LangFile.prefix + "请正确输入数字！");
                return false;
            }

            GlobalInfo globalInfo = TargetManager.getGlobalInfo();
            Player player = Bukkit.getPlayer(playerName);

            switch (action) {
                case "delete":
                    return handleDelete(sender, playerName, player, count, globalInfo);

                case "add":
                    return handleAdd(sender, playerName, player, count, globalInfo, args);

                default:
                    sender.sendMessage(LangFile.prefix + "未知操作，请使用 add/delete");
                    return false;
            }
        }
        sender.sendMessage(LangFile.prefix + LangFile.needPermissionMsg); // 权限不足消息
        return false; // 命令未成功执行
    }

    private boolean handleDelete(CommandSender sender, String playerName, Player player, int count, GlobalInfo globalInfo) {
        if (player != null) {
            return processDeleteForOnlinePlayer(sender, player, count, globalInfo);
        } else {
            return processDeleteForOfflinePlayer(sender, playerName, count, globalInfo);
        }
    }

    private boolean processDeleteForOnlinePlayer(CommandSender sender, Player player, int count, GlobalInfo globalInfo) {
        String realPlayer = player.getName();
        PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);
        int piCount = playerInfo.getAmount();

        if (piCount < count) {
            sender.sendMessage(LangFile.prefix + "不能把玩家的顶贴次数设为负数哦！当前次数为：" + piCount);
            return false;
        }

        if (DatabaseManager.getDbManager().deleteRecords(realPlayer, count)) {
            RewardGUI.cachedRewardGUI.clear();
            MainGUI.cachedMainGUI.clear();
            TargetManager.removePlayerInfo(realPlayer);
            globalInfo.deleteAmount(count);
            playerInfo.deleteAmount(count);
            sender.sendMessage(LangFile.prefix + "成功减少玩家 " + realPlayer + " 的顶贴次数，当前次数：" + (piCount - count));
            return true;
        } else {
            sender.sendMessage(LangFile.prefix + "删除失败！请查看后台报错！");
            return false;
        }
    }

    private boolean processDeleteForOfflinePlayer(CommandSender sender, String playerName, int count, GlobalInfo globalInfo) {
        if (!DatabaseManager.getDbManager().playerDataExists(playerName)) {
            sender.sendMessage(LangFile.prefix + "未找到名为 " + playerName + " 的玩家数据。离线玩家请注意大小写！");
            return false;
        }

        int dbCount = DatabaseManager.getDbManager().countPlayerRecords(playerName);
        if (dbCount < count) {
            sender.sendMessage(LangFile.prefix + "不能把玩家的顶贴次数设为负数哦！当前次数为：" + dbCount);
            return false;
        }

        if (DatabaseManager.getDbManager().deleteRecords(playerName, count)) {
            sender.sendMessage(LangFile.prefix + "成功减少玩家 " + playerName + " 的顶贴次数，当前次数：" + (dbCount - count));
            globalInfo.deleteAmount(count);
            return true;
        } else {
            sender.sendMessage(LangFile.prefix + "删除失败！请查看后台报错！");
            return false;
        }
    }

    private boolean handleAdd(CommandSender sender, String playerName, Player player, int count, GlobalInfo globalInfo, String[] args) {
        String timeStr = TimeUtil.convertTimestamp(System.currentTimeMillis());

        if (player != null) {
            return processAddForOnlinePlayer(sender, player, count, globalInfo, timeStr);
        } else {
            return processAddForOfflinePlayer(sender, playerName, count, globalInfo, args, timeStr);
        }
    }

    private boolean processAddForOnlinePlayer(CommandSender sender, Player player, int count, GlobalInfo globalInfo, String timeStr) {
        String realPlayer = player.getName();

        for (int i = 0; i < count; i++) {
            DatabaseManager.getDbManager().insertPlayerData(realPlayer, timeStr);
        }

        TargetManager.removePlayerInfo(realPlayer);
        globalInfo.addAmount(count);
        RewardGUI.cachedRewardGUI.clear();
        MainGUI.cachedMainGUI.clear();
        sender.sendMessage(LangFile.prefix + "成功增加玩家 " + realPlayer + " 的顶贴次数，次数：" + count);
        return true;
    }

    private boolean processAddForOfflinePlayer(CommandSender sender, String playerName, int count, GlobalInfo globalInfo, String[] args, String timeStr) {
        if (args.length == 5 && args[4].equals("-force")) {
            for (int i = 0; i < count; i++) {
                DatabaseManager.getDbManager().insertPlayerData(playerName, timeStr);
            }
            sender.sendMessage(LangFile.prefix + "成功强制增加一个新玩家 " + playerName + " 的顶贴次数，次数：" + count);
            return true;
        }

        if (DatabaseManager.getDbManager().playerDataExists(playerName)) {
            for (int i = 0; i < count; i++) {
                DatabaseManager.getDbManager().insertPlayerData(playerName, timeStr);
            }
            globalInfo.addAmount(count);
            sender.sendMessage(LangFile.prefix + "成功增加玩家 " + playerName + " 的顶贴次数，次数：" + count);
            return true;
        } else {
            sender.sendMessage(LangFile.prefix + "未找到名为 " + playerName + " 的玩家数据。离线玩家请注意大小写！如果需要强制添加玩家数据，请添加-force参数");
            return false;
        }
    }
}