package com.mythmc.commands.command.sub;

import com.mythmc.commands.command.MainCommands;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.MessageUtil;
import org.bukkit.entity.Player;

public class Open {
    // 处理 open 子命令
    public static boolean handle(Player player, String[] args) {
        if (player == null) {
            Debugger.logger("只能是玩家对象！");
            return false; // 发送者不是玩家
        }
        GlobalInfo globalInfo = TargetManager.getGlobalInfo();
        String cooldownTimeStr = globalInfo.getCooldownTimeStr();
        Debugger.logger("从缓存库中获取到下一次可顶贴的时间为 " + cooldownTimeStr);

        long currentTime = System.currentTimeMillis();
        String playerName = player.getName();
        long lastUsed = MainCommands.cooldownMap.getOrDefault(playerName, 0L);

        if (currentTime - lastUsed < ConfigFile.openInterval * 1000L) {
            MessageUtil.sendMessage(player, LangFile.openIntervalMsg.replace("%time%", String.valueOf(ConfigFile.openInterval)));
            return false; // 冷却中，命令未成功执行
        }

        if (args.length == 1) {
            MainGUI.openMenu(player); // 打开主菜单
        } else {
            switch (args[1].toLowerCase()) {
                case "main":
                    MainGUI.openMenu(player); // 打开主菜单
                    break;
                case "reward":
                    RewardGUI.openMenu(player); // 打开奖励菜单
                    break;
                default:
                    MessageUtil.sendMessage(player, "可用的菜单为main或reward"); // 不支持的菜单类型消息
                    return false;
            }
        }

        MainCommands.cooldownMap.put(playerName, currentTime);
        return true;
    }
}
