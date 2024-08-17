package com.mythmc.commands.command.sub;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.api.event.PlayerTopperRewardClaimEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Claim {

    // 定义一个私有静态变量，用于表示是否正在检查中，用于锁住进程，防止玩家多次请求重复领奖
    private static boolean isChecking = false;
    public static boolean handle(Player player, MineBBSTopper plugin) {
        if (player != null) {
            // 获取当前玩家的名字
            String playerName = player.getName();

            // 记录玩家尝试领取奖励的日志
            Debugger.logger("§a玩家 " + playerName + " 正在尝试领取奖励...");

            // 检查是否有正在进行的进程
            if (isChecking) {
                // 有进程在处理则发送消息给玩家，告知处理中的状态
                MessageUtil.sendMessage(player, LangFile.processing);
                // 记录日志，说明领取奖励失败的原因是已有进程在运行
                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n已有进程在运行");
                return false; // 结束方法，返回失败
            }

            // 检查当前时间是否在允许领取奖励的时间段内
            if (TimeUtil.isCurrentHourWithinSpan()) {
                // 获取 GlobalInfo 对象
                GlobalInfo globalInfo = TargetManager.getGlobalInfo();
                // 获取冷却时间戳
                long cooldownTimestamp = globalInfo.getCooldownTimestamp();
                // 获取冷却时间文本格式
                String cooldownTimeStr = globalInfo.getCooldownTimeStr();

                // 获取当前时间的时间戳
                long currentTime = System.currentTimeMillis();

                // 如果冷却时间戳小于10亿，表示单位是秒，需要转换为毫秒，兼容低版本格式
                if (cooldownTimestamp < 10000000000L) {
                    cooldownTimestamp = cooldownTimestamp * 1000L;
                }
                // 如果冷却时间戳大于等于当前时间，表示还在冷却中
                if (cooldownTimestamp >= currentTime) {
                    // 发送消息给玩家，告知冷却时间
                    MessageUtil.sendMessage(player, LangFile.upIntervalMsg.replace("%time%", cooldownTimeStr));
                    // 记录日志，说明领取奖励失败的原因是冷却中
                    Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n顶贴冷却中");
                    return false; // 结束方法，返回失败
                }

                // 获取玩家的相关信息
                PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);

                // 如果启用了限制功能
                if (ConfigFile.enableLimit) {
                    // 获取玩家今天已领取的奖励次数
                    int todayAmount = playerInfo.getTodayAmount();

                    // 如果今天已领取次数超过限制
                    if (todayAmount > ConfigFile.limitTimes - 1) {
                        // 发送消息给玩家，告知已超过当日限制次数
                        MessageUtil.sendMessage(player, LangFile.limited.replace("%limit%", String.valueOf(ConfigFile.limitTimes)));
                        // 记录日志，说明领取奖励失败的原因是超过当日限制次数
                        Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n超过当日限制次数：" + todayAmount);
                        return false; // 结束方法，返回失败
                    }
                }

                // 开始检查顶贴状态。锁定进程，防止因网络波动导致玩家多次领奖
                isChecking = true;
                // 记录日志，说明正在检测顶贴状态并锁定进程
                Debugger.logger("正在为玩家 " + playerName + " 检测顶贴状态，进程锁定中");

                // 异步获取网页中的时间元素
                MineBBSHtmlParser.fetchTimeElementsAsync(status -> {

                    // 检查获取到的时间元素是否为空
                    if (status != null && !status.isEmpty()) {
                        // 如果获取到的时间元素为 "true"，表示可以领取奖励
                        switch (status) {
                            case "true":
                                // 计算下次可领取奖励的时间戳
                                long endTimestamp = currentTime / 1000 + ConfigFile.cooldown;
                                // 转换为可读的时间数据
                                String endTimeData = TimeUtil.convertTimestamp(endTimestamp);

                                // 记录日志，设置缓存库中的下一次可顶贴时间
                                Debugger.logger("设置缓存库中下一次可顶贴的时间为 " + endTimeData);

                                // 处理命令列表
                                List<String> handleCommands = OffdayUtil.handleCommands();

                                // 更新玩家的奖励信息，并更新数据库
                                playerInfo.addAmount(1);
                                playerInfo.setLastTimeStr(TimeUtil.convertTimestamp(currentTime));
                                playerInfo.addTodayAmount(1);

                                // 更新全局冷却信息，并更新数据库
                                globalInfo.addAmount(1);
                                globalInfo.setCooldownTimestamp(endTimestamp);
                                globalInfo.setCooldownTimeStr(endTimeData);

                                // 向其它子服发送更新缓存消息
                                PluginMsgListener.sendUpdateRequestToBungeeCord();

                                // 在主线程中执行以下代码操作
                                HandySchedulerUtil.runTask(() -> {
                                    // 创建玩家普通领奖事件
                                    PlayerTopperRewardClaimEvent event = new PlayerTopperRewardClaimEvent(playerInfo, handleCommands, endTimestamp);
                                    // 调用插件管理器的事件
                                    plugin.getServer().getPluginManager().callEvent(event);

                                    // 如果事件没有被取消
                                    if (!event.isCancelled()) {
                                        try {
                                            // 执行奖励命令
                                            CommandUtil.executeCommands(player, event.getRewardCommand());
                                            // 记录日志，奖励已发放
                                            Debugger.logger("已为玩家 " + playerName + " 发放普通顶贴奖励");
                                        } catch (Exception e) {
                                            // 记录日志，执行奖励命令时出错
                                            Debugger.logger("执行顶贴领奖命令时出错: " + e.getMessage());
                                        }
                                    } else {
                                        // 记录日志，奖励被取消
                                        Debugger.logger("玩家 " + playerName + " 的顶贴奖励被其他插件取消");
                                    }
                                });

                                break;
                            case "false":
                                // 如果获取到的时间元素不为 "true"，表示已经超出了领取时间
                                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n已超过十分钟领取时间");
                                // 发送消息给玩家，告知领取失败
                                MessageUtil.sendMessage(player, LangFile.failMsg);
                                break;
                            case "timeout":
                                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n网页连接超时");
                                MessageUtil.sendMessage(player, LangFile.timeoutMsg);
                                break;
                            case "error":
                                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n网页连接失败");
                                // 发送消息给玩家，告知网络错误
                                MessageUtil.sendMessage(player, LangFile.networkError);
                                break;
                        }
                    }
                    // 释放进程锁定
                    isChecking = false;
                    // 记录日志，说明检测顶贴状态成功，进程已释放
                    Debugger.logger("§a为玩家 " + playerName + " 检测顶贴状态成功，进程已释放");
                });
            } else {
                // 如果当前时间不在可顶贴时间段内
                Debugger.logger("玩家 " + playerName + " 尝试获取奖励失败。原因：§n未在可顶贴时间段内");
                // 发送消息给玩家，告知未在可领取时间段内
                MessageUtil.sendMessage(player, LangFile.waitMsg.replace("%time%", ConfigFile.claimSpan));
            }
            return true; // 返回成功
        }
        Debugger.logger("§c这个指令只能玩家使用！");
        return false;
    }
}
