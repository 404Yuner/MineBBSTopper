package com.mythmc.tools;

import com.mythmc.MineBBSTopper;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Debugger {
    private static boolean debugMode;
    private static final ConsoleCommandSender CONSOLE = Bukkit.getServer().getConsoleSender();
    public Debugger() {}

    public void load() {
        debugMode = MineBBSTopper.INSTANCE.getConfig().getBoolean("DebugMode",true);
        if (debugMode) {
            logger("§a已开启DEBUG模式，后台记录将会变多");
        }
    }
    public static void logger(String meg) {
        if (debugMode) {
            CONSOLE.sendMessage("§7[§bMineBBSTopper§7] §6日志 §8| §7" + meg);
        }
    }
}
