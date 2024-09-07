package com.mythmc.tools.utils;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import com.cryptomorin.xseries.XSound;
import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.externs.hook.economy.VaultHook;
import com.mythmc.externs.hook.economy.PlayerPointsHook;
import com.mythmc.events.listener.bungee.PluginMsgListener;
import com.mythmc.tools.utils.type.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandUtil {

    public static void executeCommands(Player player, List<String> commandList) {
        int delaySeconds = 0; // 初始化延迟时间为0秒

        for (String action : commandList) {
            if (action.startsWith("[delay]")) {
                // 如果指令是延迟指令，则解析出延迟时间并设置
                delaySeconds = Integer.parseInt(action.split("]")[1]);
            } else {
                // 如果不是延迟指令，则添加到延迟执行列表中
                HandySchedulerUtil.runTaskLater(() -> executeCommand(player, action), delaySeconds * 20L); // 将延迟时间转换为tick数
            }
        }
    }

    private static void executeCommand(Player player, String action) {
        if (action.startsWith("[")) {
            // 分割原始内容
            String[] parts = action.split("]", 2);
            // 找出动作类型
            String typeString = parts[0].substring(1).toLowerCase();
            ActionType type = ActionType.fromString(typeString.toLowerCase());
            if (type == null) {
                // 如果无法匹配到有效的 ActionType，执行原始的内容
                player.performCommand(action);
                return;
            }

            String commandContent = setString(player, parts);

            // 初始化命令内容
            String commandToExecute;
            // 分割条件与命令内容
            String[] commandParts = commandContent.split("@condition:", 2);
            // 如果找到condition
            if (commandParts.length > 1) {
                // 处理条件里的空格
                String condition = commandParts[1];
                // 处理条件
                if (!ConditionUtil.checkCondition(condition)) {
                    return;
                } else {
                    // 返回满足条件的动作组
                    commandToExecute = ColorUtil.colorize(commandParts[0]);
                }
            } else {
                // 如果找不到condition直接返回原始的内容
                commandToExecute = ColorUtil.colorize(commandContent);
            }

            switch (type) {
                case CONSOLE:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
                    break;
                case PLAYER:
                    PlayerSchedulerUtil.performCommand(player, commandToExecute);
                  //  player.performCommand(commandToExecute);
                    break;
                case TELL:
                    player.sendMessage(commandToExecute);
                    break;
                case SOUND:
                    XSound.matchXSound(commandToExecute).get().play(player);
                    break;
                case BROADCAST:
                    PluginMsgListener.sendAlertToBungeeCord(commandToExecute);
                    break;
                case GIVE_MONEY:
                    VaultHook.giveMoney(player, String.valueOf(getRandomNumberInRange(commandToExecute)));
                    break;
                case GIVE_POINTS:
                    PlayerPointsHook.givePoints(player, String.valueOf(getRandomNumberInRange(commandToExecute)));
                    break;
                case CLOSE:
                    player.closeInventory();
                    break;
                case OPEN:
                    handleOpenAction(player, commandToExecute);
                    break;
                case TITLE:
                    handleTitleAction(player, commandToExecute);
                    break;
                case BOSSBAR:
                    handleBossBarAction(player, commandToExecute);
                    break;
                case CONNECT:
                    handleConnectAction(player, commandToExecute);
                    break;
                case OP:
                    handleOpAction(player, commandToExecute);
                    break;
                case NULL:
                    break;
            }
        } else {
            player.performCommand(action);
        }
    }

    @NotNull
    private static String setString(Player player, String[] parts) {
        // 获取原始内容
        String commandContent = parts[1];
        // 连续替换所有占位符
        return PAPIUtil.set(player,commandContent
                .replace("%serverUrl%", ConfigFile.url)
                .replace("%techUrl%", ConfigFile.techUrl)
                .replace("{player}", player.getName())
                .replace("%player%", player.getName())
                .replace("%prefix%", LangFile.prefix));
    }

    public static void broadcast(String meg) {
        // 分割段落
        String message = meg.replace("\\n", "\n"); // 将字符串中的 \n 转换为换行符号
        // 获取在线玩家流，并对每个玩家发送消息
        Bukkit.getServer().getOnlinePlayers().forEach(player ->
                Arrays.stream(message.split("\n")) // 将消息分割成多行，并为每行创建一个流
                        .forEach(player::sendMessage) // 对每行消息，向玩家发送
        );
    }

    public static int getRandomNumberInRange(String origin) {
        try {
            // 预判断时间格式是否正确
            if (!origin.contains("-")) {
                // 强制转换double为int类型
                return (int) Math.round(Double.parseDouble(origin));
            } else {
                // 开始分割时间
                String[] range = origin.split("-");
                if (range.length != 2) {
                    // 不存在符号则抛出错误
                    throw new IllegalArgumentException("未知格式 用符号-分割");
                }
                // 转换
                int min = (int) Math.round(Double.parseDouble(range[0]));
                int max = (int) Math.round(Double.parseDouble(range[1]));
                // 比大小
                if (min >= max) {
                    throw new IllegalArgumentException("左边不能大于右边");
                }
                // 随机值对象
                Random r = new Random();
                // 因为是从0开始，所以得+1
                return r.nextInt((max - min) + 1) + min;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("错误数字格式: " + origin, e);
        }
    }
    private static void handleOpenAction(Player player, String command) {
        player.closeInventory();
        if (command.equalsIgnoreCase("main")) {
            MainGUI.openMenu(player);
        } else {
            if (GUIFile.rewardMenuEnable) {
                RewardGUI.openMenu(player);
            } else {
                player.sendMessage(LangFile.prefix + LangFile.rewardUntenable);
            }
        }
    }

    private static void handleTitleAction(Player player, String command) {
        String[] args = command.split(" ");
        String title = args[0];
        String subTitle = args.length > 1 ? args[1] : " ";
        int fadeIn = args.length > 3 ? Integer.parseInt(args[2]) : 10;
        int stay = args.length > 4 ? Integer.parseInt(args[3]) : 60;
        int fadeOut = args.length > 5 ? Integer.parseInt(args[4]) : 10;
        player.getPlayer().sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    private static void handleBossBarAction(Player player, String command) {
        String[] args = command.split(" ");
        String message = args[0];
        String colorName = args[1].toUpperCase();
        int time = args.length > 2 ? Integer.parseInt(args[2]) : 15;

        try {
            BarColor color = BarColor.valueOf(colorName);
            BossBar bossBar = Bukkit.createBossBar(message, color, BarStyle.SOLID);
            bossBar.setProgress(1.0);
            bossBar.addPlayer(player);

            for (int i = 0; i <= time; i++) {
                final double progress = 1.0 - ((double) i / time);
                HandySchedulerUtil.runTaskLater(() -> bossBar.setProgress(progress), 20L * i);
            }

            HandySchedulerUtil.runTaskLater(() -> bossBar.removePlayer(player), 20L * time);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            player.sendMessage(LangFile.prefix + "§c错误: 无效的BOSSBAR颜色。请联系管理员使用正确的颜色名称。");
        }
    }

    private static void handleConnectAction(Player player, String command) {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);
            out.writeUTF("Connect");
            out.writeUTF(command);
            player.sendPluginMessage(MineBBSTopper.INSTANCE, "BungeeCord", byteArray.toByteArray());
        } catch (Exception ex) {
            player.sendMessage(LangFile.prefix + "§c在连接子服时发生错误，请联系管理员查看报错。");
            ex.printStackTrace();
        }
    }

    private static void handleOpAction(Player player, String command) {
        boolean isOp = player.isOp();
        try {
            if (!isOp) {
                player.setOp(true);
            }
            player.performCommand(command);
        } finally {
            if (!isOp) {
                player.setOp(false);
            }
        }
    }
}
