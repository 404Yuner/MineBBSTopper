package com.mythmc.commands.command.sub;

import com.mythmc.MineBBSTopper;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.file.statics.LangFile;
import org.bukkit.command.CommandSender;

public class Reload {
    // 处理 reload 子命令
    public boolean handle(CommandSender sender, MineBBSTopper plugin) {
        if (sender.hasPermission("minebbstopper.reload") || sender.getName().equalsIgnoreCase("CONSOLE")) {
            plugin.onReload(); // 调用插件的重新加载方法
            // 清除缓存
            MainGUI.cachedMainGUI.clear();
            RewardGUI.cachedRewardGUI.clear();
            sender.sendMessage(LangFile.prefix + "重载成功，若更改数据库配置无效请重启!");
            return true; // 命令成功执行
        }
        sender.sendMessage(LangFile.prefix + LangFile.needPermissionMsg); // 权限不足消息
        return false; // 命令未成功执行
    }
}
