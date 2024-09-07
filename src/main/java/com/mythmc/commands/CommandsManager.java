package com.mythmc.commands;

import com.mythmc.MineBBSTopper;
import com.mythmc.commands.command.MainCommands;
import com.mythmc.commands.command.tab.MainTabCompleter;

public class CommandsManager {
    // 定义一个插件实例，MineBBSTopper 类型
    private final MineBBSTopper plugin;

    // 构造函数，初始化 plugin 变量
    public CommandsManager(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    // 加载命令执行器，并注册命令
    public void load() {
        // 注册 /minebbstopper 命令的执行器
        plugin.getCommand("minebbstopper").setExecutor(new MainCommands(plugin));
        plugin.getCommand("minebbstopper").setTabCompleter(new MainTabCompleter());
        // 输出注册成功的日志信息
        plugin.logger("§a注册 §8| §a成功注册插件主指令");
    }


}
