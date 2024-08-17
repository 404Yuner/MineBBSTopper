package com.mythmc.commands.command.sub;

import com.mythmc.events.listener.bungee.PluginMsgListener;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.CommandUtil;
import com.mythmc.tools.utils.MessageUtil;
import org.bukkit.entity.Player;

public class Test {
    // 处理 test 子命令
    public static boolean handle(Player player, String[] args) {
        if (player == null) {
            Debugger.logger("§c这个指令只能玩家使用！");
            return false; // 发送者不是玩家
        }
        if (args.length == 2) {
            if (player.hasPermission("minebbstopper.test")) {
                switch (args[1].toLowerCase()) {
                    case "normal":
                        CommandUtil.executeCommands(player, ConfigFile.normalRewardCommands);
                        break;
                    case "offday":
                        CommandUtil.executeCommands(player, ConfigFile.offdayRewardCommands);
                        break;
                    case "msg":
                        PluginMsgListener.sendUpdateRequestToBungeeCord();
                        break;
                    default:
                        MessageUtil.sendMessage(player, "可用奖励类型为 normal 或 offday"); // 不支持的奖励类型消息
                        return false;
                }
                return true; // 命令成功执行
            }
            MessageUtil.sendMessage(player, LangFile.needPermissionMsg); // 权限不足消息
        }
        return false; // 命令未成功执行
    }

}
