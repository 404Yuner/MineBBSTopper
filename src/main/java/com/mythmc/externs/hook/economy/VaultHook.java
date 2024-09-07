package com.mythmc.externs.hook.economy;

import com.mythmc.file.statics.LangFile;
import com.mythmc.tools.utils.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy economy;
    private static final String ERROR_PREFIX = "\n" + LangFile.prefix + "§c";

    public static boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();
        return true;
    }

    public static boolean hasValidEconomy() {
        return economy != null;
    }

    public static void giveMoney(Player player, String amount) {
        if (!hasValidEconomy()) {
            sendErrorMessage(player, "经济系统暂未开启，请联系管理员！待发放金额为: " + amount);
            return;
        }

        try {
            double money = Double.parseDouble(amount);
            if (money < 0.0) {
                sendErrorMessage(player, "金币奖励配置错误，错误的金额为: " + amount);
                return;
            }
            economy.depositPlayer(player.getName(), player.getWorld().getName(), money);
            MessageUtil.sendMessage(player, "§a您已获得 " + money + " 游戏币");
        } catch (NumberFormatException e) {
            sendErrorMessage(player, "金币奖励转换错误，错误的金额为: " + amount);
        }
    }

    private static void sendErrorMessage(Player player, String message) {
        player.sendMessage(ERROR_PREFIX + message + "\n请将此消息截图后发送给管理员确认！");
    }
}