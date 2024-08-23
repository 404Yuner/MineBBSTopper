package com.mythmc.commands.command;

import com.mythmc.MineBBSTopper;
import com.mythmc.commands.command.sub.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

// 定义一个主类 MainCommands，实现 CommandExecutor 和 TabCompleter 接口
public class MainCommands implements CommandExecutor {

    // 定义一个插件实例，MineBBSTopper 类型
    private final MineBBSTopper plugin;

    // 定义一个静态的 HashMap，用于存储玩家的冷却时间
    public static final Map<String, Long> cooldownMap = new HashMap<>();

    // 构造函数，初始化 plugin 变量
    public MainCommands(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    // 处理命令执行的逻辑
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    return Reload.handle(sender, plugin);
                case "open":
                    return Open.handle(player, args);
                case "test":
                    return Test.handle(player, args);
                case "url":
                    return URL.handle(sender);
                case "claim":
                    return Claim.handle(player, plugin);
                default:
                    sender.sendMessage("无效的子命令！");
                    return false;
            }
        }
        return false;
    }

}
