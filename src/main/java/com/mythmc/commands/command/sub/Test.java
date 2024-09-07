package com.mythmc.commands.command.sub;

import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.commands.gui.external.RewardData;
import com.mythmc.events.listener.bungee.PluginMsgListener;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.CommandUtil;
import com.mythmc.tools.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Test {

    // 处理 test 子命令
    public boolean handle(Player player, String[] args) {
        if (player == null) {
            Debugger.logger("§c这个指令只能玩家使用！");
            return false; // 发送者不是玩家
        }

        if (args.length < 2) {
            return false; // 参数不足，直接返回
        }

        if (!player.hasPermission("minebbstopper.test")) {
            MessageUtil.sendMessage(player, LangFile.needPermissionMsg);
            return false; // 权限不足消息
        }

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
            case "reward":
                handleRewardCommand(player, args);
                break;
            default:
                MessageUtil.sendMessage(player, "可用奖励类型为 normal 或 offday");
                return false; // 不支持的奖励类型消息
        }

        return true; // 命令成功执行
    }

    private  void handleRewardCommand(Player player, String[] args) {
        if (args.length < 3) {
            CommandUtil.executeCommands(player, Collections.singletonList("参数缺失，请输入奖励编号"));
            return;
        }

        try {
            int rewardLabel = Integer.parseInt(args[2]);
            List<String> rewardCommands = getCommandsByRewardLabel(player, rewardLabel);
            CommandUtil.executeCommands(player, rewardCommands);
        } catch (NumberFormatException e) {
            CommandUtil.executeCommands(player, Collections.singletonList("[tell]%prefix%reward 后的参数必须为 1-10 之间的整数"));
        }
    }

    // 根据 rewardLabel 获取对应的命令列表
    public  List<String> getCommandsByRewardLabel(Player player, int rewardLabel) {
        int MIN_REWARD_LABEL = 1;
        int MAX_REWARD_LABEL = 10;
        if (rewardLabel < MIN_REWARD_LABEL || rewardLabel > MAX_REWARD_LABEL) {
            MessageUtil.sendMessage(player, "测试的奖励标识必须在 " + MIN_REWARD_LABEL + " 到 " + MAX_REWARD_LABEL + " 之间");
            return Collections.emptyList(); // 提前返回空列表
        }

        for (RewardData rewardData : RewardGUI.slotData.values()) {
            if (rewardData.getRewardLabel() == rewardLabel) {
                return rewardData.getCommands();
            }
        }

        return Collections.singletonList("[tell]%prefix%此奖励暂未配置内容，请尝试其它编号。当前编号：" + rewardLabel);
    }
}