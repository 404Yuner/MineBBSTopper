package com.mythmc.commands.command.sub;

import com.mythmc.file.statics.LangFile;
import com.mythmc.tools.remote.core.MineBBSHtmlParser;
import org.bukkit.command.CommandSender;

public class URL {
    // 处理 url 子命令
    public static boolean handle(CommandSender sender) {
        if (sender.hasPermission("minebbstopper.reload") || sender.getName().equalsIgnoreCase("CONSOLE")) {
            MineBBSHtmlParser.fetchTimeElementsAsync(status -> sender.sendMessage("§a请查看控制台信息！获取状态：" + status));
            return true; // 命令成功执行
        }
        sender.sendMessage(LangFile.prefix + LangFile.needPermissionMsg); // 权限不足消息
        return false; // 命令未成功执行
    }
}
