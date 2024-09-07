package com.mythmc.commands.command.sub;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.api.event.PlayerTopperRewardClaimEvent;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.events.listener.bungee.PluginMsgListener;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.remote.core.MineBBSHtmlParser;
import com.mythmc.tools.utils.CommandUtil;
import com.mythmc.tools.utils.MessageUtil;
import com.mythmc.tools.utils.OffdayUtil;
import com.mythmc.tools.utils.TimeUtil;
import org.bukkit.entity.Player;

import java.util.List;
public class Claim {

    private boolean isChecking = false;

    public boolean handle(Player player, MineBBSTopper plugin) {
        if (player != null) {
            String playerName = player.getName();
            Debugger.logger("§a玩家 " + playerName + " 正在尝试领取奖励...");
            
            if (!canClaimReward(player)) {
                return false;
            }

            isChecking = true;
            Debugger.logger("正在为玩家 " + playerName + " 检测顶贴状态，进程锁定中");

            MineBBSHtmlParser.fetchTimeElementsAsync(status -> {
                processRewardStatus(status, player, plugin);
                isChecking = false;
                Debugger.logger("§a为玩家 " + playerName + " 检测顶贴状态成功，进程已释放");
            });

            return true;
        }
        Debugger.logger("§c这个指令只能玩家使用！");
        return false;
    }

    private boolean canClaimReward(Player player) { // 判断各种条件
        String playerName = player.getName();

        if (isChecking) {
            MessageUtil.sendMessage(player, LangFile.processing);
            Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n已有检测进程在运行");
            return false;
        }
        if (!TimeUtil.isCurrentHourWithinSpan()) {
            Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n未在设置的可顶贴时间段内");
            MessageUtil.sendMessage(player, LangFile.waitMsg.replace("%time%", ConfigFile.claimSpan));
            return false;

        }

        GlobalInfo globalInfo = TargetManager.getGlobalInfo();
        long cooldownTimestamp = globalInfo.getCooldownTimestamp();
        long currentTime = System.currentTimeMillis();

        // 兼容格式
        if (cooldownTimestamp < 10000000000L) {
            cooldownTimestamp *= 1000L;
        }

        if (cooldownTimestamp >= currentTime) {
            String cooldownTimeStr = globalInfo.getCooldownTimeStr();
            MessageUtil.sendMessage(player, LangFile.upIntervalMsg.replace("%time%", cooldownTimeStr));
            Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n顶贴冷却中，下一次可顶贴：" + cooldownTimeStr);
            return false;
        }

        if (ConfigFile.enableLimit) {
            PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);
            int todayAmount = playerInfo.getTodayAmount();
            if (todayAmount > ConfigFile.limitTimes - 1) {
                MessageUtil.sendMessage(player, LangFile.limited.replace("%limit%", String.valueOf(ConfigFile.limitTimes)));
                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n超过当日限制次数，当日次数：" + todayAmount);
                return false;
            }
        }

        return true;
    }

    private  void processRewardStatus(String status, Player player, MineBBSTopper plugin) {
        String playerName = player.getName();
        long currentTime = System.currentTimeMillis();
        PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);
        GlobalInfo globalInfo = TargetManager.getGlobalInfo();

        switch (status) {
            case "true":
                long endTimestamp = currentTime / 1000 + ConfigFile.cooldown;
                String endTimeData = TimeUtil.convertTimestamp(endTimestamp);

                List<String> handleCommands = OffdayUtil.handleCommands();

                playerInfo.addAmount(1);
                playerInfo.setLastTimeStr(TimeUtil.convertTimestamp(currentTime));
                playerInfo.addTodayAmount(1);

                globalInfo.addAmount(1);
                globalInfo.setCooldownTimestamp(endTimestamp);
                globalInfo.setCooldownTimeStr(endTimeData);
                Debugger.logger("设置缓存库中下一次可顶贴的时间为 " + endTimeData);

                // 清除旧的菜单缓存
                MainGUI.cachedMainGUI.clear();
                RewardGUI.cachedRewardGUI.clear();

                // 向其余子服发送更新全局数据消息
                PluginMsgListener.sendUpdateRequestToBungeeCord();

                // 主线程回调处理事件
                HandySchedulerUtil.runTask(() -> {
                    PlayerTopperRewardClaimEvent event = new PlayerTopperRewardClaimEvent(playerInfo, handleCommands, endTimestamp);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        try {
                            CommandUtil.executeCommands(player, event.getRewardCommand());
                            Debugger.logger("已为玩家 " + playerName + " 发放普通顶贴奖励");
                        } catch (Exception e) {
                            Debugger.logger("执行顶贴领奖命令时出错: " + e.getMessage());
                        }
                    } else {
                        Debugger.logger("玩家 " + playerName + " 的顶贴奖励被其他插件取消");
                    }
                });

                break;
            case "false":
                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n已超过十分钟领取时间");
                MessageUtil.sendMessage(player, LangFile.failMsg);
                break;
            case "timeout":
                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n网页连接超时");
                MessageUtil.sendMessage(player, LangFile.timeoutMsg);
                break;
            case "error":
                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n网页连接失败");
                MessageUtil.sendMessage(player, LangFile.networkError);
                break;
        }
    }
}