package com.mythmc;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.commands.CommandsManager;
import com.mythmc.commands.command.MainCommands;

import com.mythmc.commands.gui.external.CustomInventory;
import com.mythmc.events.listener.GUIListener;
import com.mythmc.externs.hook.hologram.DecentHologramsHook;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.database.DatabaseManager;
import com.mythmc.events.EventsManager;
import com.mythmc.externs.HookManager;
import com.mythmc.externs.hook.placeholders.PlaceholderCache;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.FileManager;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.events.listener.bungee.PluginMsgListener;
import com.mythmc.tools.remote.WebPreChecker;
import com.mythmc.tools.local.AutoUpdater;
import com.mythmc.tools.remote.Metrics;
import com.mythmc.tools.remote.UpdateChecker;

import com.mythmc.tools.utils.RefreshUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;


public final class MineBBSTopper extends JavaPlugin {
    public static MineBBSTopper INSTANCE;
    private static final String VERSION = Bukkit.getVersion();
    public static final boolean isLowVersion = VERSION.contains("1.5") || VERSION.contains("1.6") || VERSION.contains("1.7") || VERSION.contains("1.8") || VERSION.contains("1.9") || VERSION.contains("1.10") || VERSION.contains("1.11") || VERSION.contains("1.12");
    public static boolean BUNGEECORE = false;
    public static final String AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    public static final ConsoleCommandSender CONSOLE = Bukkit.getServer().getConsoleSender();

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        INSTANCE = this;
        this.pluginBanner();
        HandySchedulerUtil.init(this);
        (new AutoUpdater(this)).load();
        (new FileManager(this)).load();
        (new DatabaseManager(this)).load();
        (new PluginMsgListener(this)).load();
        (new EventsManager(this)).load();
        (new CommandsManager(this)).load();
        (new HookManager(this)).load();
      //  (new ParserManagerFactory()).load();
        (new WebPreChecker(this)).load();

        Metrics metrics = new Metrics(this,22565);
        metrics.addCustomChart(new Metrics.SingleLineChart("topper_count", () -> TargetManager.getGlobalInfo().getAmount()));
        metrics.addCustomChart(new Metrics.AdvancedPie("database_type", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put(ConfigFile.DataType.toUpperCase(), 1);
            return valueMap;
        }));

        logger("§b信息 §8| §3插件加载完成，总耗时: §a" + (System.currentTimeMillis() - startTime) + "ms");
        logger("§b信息 §8| §3您正在使用版本: §av" + getDescription().getVersion() + " by 404Yuner");
        logger("§b信息 §8| §3获取支持请前往QQ群: §a301662621 §7§n（请发送尽可能完整的问题描述以及报错日志）");
        (new UpdateChecker(this)).checkForUpdates();
        (new RefreshUtil(this)).scheduleDailyTask();

    }

    @Override
    public void onDisable() {
        logger("§c卸载 §8| §6插件正在卸载中...");
        INSTANCE = null;

        (new DatabaseManager(this)).close();
        HandySchedulerUtil.cancelTask();
        logger("§c卸载 §8| §6已关闭所有正在运行的任务");
        this.cacheClear();
        logger("§c卸载 §8| §6插件的残余内容已清理完毕");
        logger("§c卸载 §8| §6插件卸载完毕，感谢您的使用。QQ交流群: 301662621");
    }

    public void onReload() {
        (new DatabaseManager(this)).close();
        logger("§a重载 §8| §6插件正在重载，若更改数据库配置无效请重启！");
        reloadConfig();
        (new FileManager(this)).load();
        (new MainGUI()).initializeSlotCommands();
        (new RewardGUI()).initializeSlotData();
        (new DatabaseManager(this)).load(); //方法不稳定，取消重载数据库
        (new WebPreChecker(this)).load();
    }
    private void pluginBanner() {
        CONSOLE.sendMessage("\n" +
                "      ___       _  _   _   __ ___ _   _   _   _  _  \n" +
                " |\\/|  |  |\\ | |_ |_) |_) (_   | / \\ |_) |_) |_ |_) \n" +
                " |  | _|_ | \\| |_ |_) |_) __)  | \\_/ |   |   |_ | \\ \n" +
                "                                                    ");
    }

    public void logger(String message) {
        CONSOLE.sendMessage("§7[§bMineBBSTopper§7] " + message);
    }

    public void cacheClear() {
        DecentHologramsHook.removeHologram();
        HookManager.clear();
        PlaceholderCache.clear();
        GUIListener.lastClickTimes.clear();
        MainCommands.cooldownMap.clear();
        ConfigFile.normalRewardCommands.clear();
        ConfigFile.offdayRewardCommands.clear();
        ConfigFile.offdays.clear();
        RewardGUI.slotData.clear();
        MainGUI.slotCommands.clear();
        CustomInventory.inventoryMap.clear();
        MainGUI.cachedMainGUI.clear();
        RewardGUI.cachedRewardGUI.clear();
    }

}
