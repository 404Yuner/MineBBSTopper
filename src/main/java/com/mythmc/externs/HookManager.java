package com.mythmc.externs;

import com.mythmc.MineBBSTopper;
import com.mythmc.commands.command.MainCommands;
import com.mythmc.externs.hook.economy.PlayerPointsHook;
import com.mythmc.externs.hook.economy.VaultHook;
import com.mythmc.externs.hook.hologram.DecentHologramsHook;
import com.mythmc.externs.hook.hologram.HolographicDisplaysHook;
import com.mythmc.externs.hook.placeholders.PlaceholderHook;
import com.mythmc.file.statics.HologramFile;
import org.bukkit.Bukkit;

public class HookManager {
    private final MineBBSTopper plugin;
    public static boolean isPlaceholderHook = false;
    public HookManager(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadEconomy();
        loadPAPI();
        loadHologram();
    }
    private void loadEconomy() {

        plugin.logger(PlayerPointsHook.hasValidPoints() ?
                "§3挂钩 §8| §a成功挂钩 §2PlayerPoints" :
                "§3挂钩 §8| §e找不到 PlayerPoints 插件，点券相关功能无法使用");

        plugin.logger(VaultHook.setupEconomy() ?
                "§3挂钩 §8| §a成功挂钩 §2Vault" :
                "§3挂钩 §8| §e找不到 Vault 插件，经济相关功能无法使用");
    }
    private void loadPAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderHook placeholder = new PlaceholderHook();
            placeholder.register();
            plugin.logger("§3挂钩 §8| §a成功挂钩 §2PlaceHolderAPI");
            isPlaceholderHook = true;
        } else {
            plugin.logger("§3挂钩 §8| §e找不到 PlaceholderAPI 插件，占位符相关功能无法使用 ");
        }
    }
    private void loadHologram() {
        if (DecentHologramsHook.isDecentHologramsEnable() && HologramFile.hologramType.equals("DecentHolograms")) {
            plugin.logger("§3挂钩 §8| §a成功挂钩 §2DecentHolograms");
            (new DecentHologramsHook()).load();
        } else {
            plugin.logger("§3挂钩 §8| §e找不到 DecentHolograms 插件，全息图排行榜功能无法使用");
        }
        if (HolographicDisplaysHook.isHolographicDisplaysEnable() && HologramFile.hologramType.equals("HolographicDisplays")) {
            plugin.logger("§3挂钩 §8| §a成功挂钩 §2HolographicDisplays");
            (new HolographicDisplaysHook()).load();
        } else {
            plugin.logger("§3挂钩 §8| §e找不到 HolographicDisplays 插件，全息图排行榜功能无法使用");
        }
    }
    public static void clear() {
        if (isPlaceholderHook) {
            PlaceholderHook placeholder = new PlaceholderHook();
            placeholder.unregister();
            MineBBSTopper.CONSOLE.sendMessage("§7[§bMineBBSTopper§7] §c卸载 §8| §6已取消注册 PlaceholderAPI ");
        }
    }
}
