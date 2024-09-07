package com.mythmc.events.listener;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.api.event.PlayerAccumulatedRewardClaimEvent;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.commands.gui.external.RewardData;
import com.mythmc.commands.gui.external.SlotData;
import com.mythmc.commands.gui.holder.GUIType;
import com.mythmc.commands.gui.holder.MineBBSTopperGUIHolder;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.CommandUtil;
import com.mythmc.tools.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIListener implements Listener {

    // 存储每个玩家最后一次点击事件的时间 玩家 / 时间
    public static Map<String, Long> lastClickTimes = new HashMap<>();

    // 处理菜单点击事件
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory().getHolder() instanceof MineBBSTopperGUIHolder) {
            event.setCancelled(true);

            // 检查事件发起者是否是玩家
            if (!(event.getWhoClicked() instanceof Player)) {
                return; // 如果不是玩家，则忽略该事件
            }
            Player player = (Player) event.getWhoClicked();
            GUIType type = ((MineBBSTopperGUIHolder) event.getInventory().getHolder()).getGuiType();
            int rawSlot = event.getRawSlot();
            switch (type) {
                case MAIN:
                    handleClickEvent(player, rawSlot, GUIType.MAIN);
                    break;
                case REWARD:
                    handleClickEvent(player, rawSlot, GUIType.REWARD);
                    break;
                default:
                    player.sendMessage(LangFile.prefix + "菜单点击事件执行错误！请将报错发送给管理员查看！");
            }
        }

    }
    private void handleClickEvent(Player player, int rawSlot, GUIType menuType) {
        // 检查是否点太快
        if (isClickTooFast(player)) {
            return;
        }
        switch (menuType) {
            case REWARD:
                // 处理 "reward" 类型的点击事件
                handleRewardClick(player, rawSlot);
                break;
            case MAIN:
                // 处理 "main" 类型的点击事件
                handleMainClick(player, rawSlot);
                break;
        }
    }
    private void handleRewardClick(Player player, int rawSlot) {
        // 在这里处理 "reward" 类型的点击事件
        String playerName = player.getName();
        // 根据槽位编号获取 SlotData 对象
        RewardData rewardSlotData = null;
        for (Map.Entry<SlotData, RewardData> entry : RewardGUI.slotData.entrySet()) {
            List<Integer> slots = entry.getKey().getSlots();
            if (slots.contains(rawSlot)) {
                // 获取对应的 SlotData 对象
                rewardSlotData = entry.getValue();
                break; // 找到对应的 SlotData 后退出循环
            }
        }

        if (rewardSlotData != null) {
            // 获取与槽位相关的命令列表、要求次数和奖励标签
            List<String> commands = rewardSlotData.getCommands();
            int require = rewardSlotData.getRequire();
            int rewardLabel = rewardSlotData.getRewardLabel();

            // 获取玩家的记录信息
            PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);
            int count = playerInfo.getAmount(); // 获取玩家记录数量
            String[] rewardData = playerInfo.getRewardData();

            // 如果奖励标签不等于 100
            if (rewardLabel != 100) {
                Debugger.logger("玩家 " + playerName + " 正在尝试获取累计" + require + "次奖励。");
                // 检查玩家是否已经领取过此奖励
                if (rewardData[rewardLabel - 1].equals("0")) {
                    // 检查玩家记录是否达到要求次数
                    if (count >= require) {
                        // 更新玩家的奖励数据
                        playerInfo.setRewardData("1", rewardLabel - 1);

                        // 移除缓存
                        RewardGUI.cachedRewardGUI.remove(playerName);

                        // 回到主线程执行奖励发放
                        HandySchedulerUtil.runTask(() -> {
                            // 创建奖励领取事件并调用
                            PlayerAccumulatedRewardClaimEvent callEvent = new PlayerAccumulatedRewardClaimEvent(playerInfo, commands, rewardLabel);
                            MineBBSTopper.INSTANCE.getServer().getPluginManager().callEvent(callEvent);

                            // 检查事件是否被取消
                            if (!callEvent.isCancelled()) {
                                try {
                                    // 关闭玩家的 GUI 界面
                                    player.closeInventory();
                                    // 执行奖励命令
                                    CommandUtil.executeCommands(player, callEvent.getRewardCommand());
                                    Debugger.logger("已为玩家 " + playerName + " 发放累计顶贴" + require + "次的奖励");
                                    // 发送提示消息
                                    MessageUtil.sendMessage(player, LangFile.rewardClaimYes.replace("%require%", String.valueOf(require)).replace("%count%", String.valueOf(count)));
                                } catch (Exception e) {
                                    Debugger.logger("执行累计顶贴" + require + "次奖励命令时出错: " + e.getMessage());
                                }
                            } else {
                                Debugger.logger("玩家 " + playerName + " 的累计顶贴奖励被其他插件取消");
                            }
                        });
                    } else {
                        // 处理不满足要求次数的情况
                        player.closeInventory();
                        MessageUtil.sendMessage(player, LangFile.rewardClaimNo.replace("%require%", String.valueOf(require)).replace("%count%", String.valueOf(count)));
                        Debugger.logger("玩家 " + playerName + " 尝试获取累计顶贴" + require + "次奖励失败。原因：§n未达到足够数量，当前为：" + count);
                    }
                } else {
                    // 奖励已领取的情况
                    MessageUtil.sendMessage(player, LangFile.rewardClaimed);
                    Debugger.logger("玩家 " + playerName + " 尝试获取累计顶贴" + require + "次奖励失败。原因：§n已领取");
                    player.closeInventory();
                }
            } else {
                // 奖励标签为 100 的情况直接执行命令
                CommandUtil.executeCommands(player, commands);
            }
        }
    }

    private void handleMainClick(Player player, int rawSlot) { // 在这里处理 "main" 类型的点击事件
        // 获取该槽位对应的命令列表
        List<String> commands = null;
        for (Map.Entry<SlotData, List<String>> entry : MainGUI.slotCommands.entrySet()) {
            List<Integer> slots = entry.getKey().getSlots();
            if (slots.contains(rawSlot)) {
                commands =  entry.getValue();
            }
        }
        if (commands != null) {
            CommandUtil.executeCommands(player, commands);
        }
    }

    private boolean isClickTooFast(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastClickTime = lastClickTimes.getOrDefault(player.getName(), 0L);
        if (currentTime - lastClickTime < ConfigFile.clickInterval * 1000D) {
            MessageUtil.sendMessage(player, "§c你点太快了，请等待 " + ConfigFile.clickInterval + " 秒");
            return true;
        }
        lastClickTimes.put(player.getName(), currentTime);
        return false;
    }


    @EventHandler
    public void onGUIOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof MineBBSTopperGUIHolder) {
            Player player = (Player) event.getPlayer();
            GUIType type = ((MineBBSTopperGUIHolder) event.getInventory().getHolder()).getGuiType();
            switch (type) {
                case MAIN:
                    if (GUIFile.mainMenuEvents != null)
                        CommandUtil.executeCommands(player, (List<String>) GUIFile.mainMenuEvents.getList("open"));
                    break;
                case REWARD:
                    if (GUIFile.rewardMenuEvents != null)
                        CommandUtil.executeCommands(player, (List<String>) GUIFile.rewardMenuEvents.getList("open"));
                    break;
                default:
                    player.sendMessage(LangFile.prefix + "菜单点击事件执行错误！请将报错发送给管理员查看！");
            }
        }
    }
    @EventHandler
    public void onGUIClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MineBBSTopperGUIHolder) {
            Player player = (Player) event.getPlayer();
            GUIType type = ((MineBBSTopperGUIHolder) event.getInventory().getHolder()).getGuiType();
            switch (type) {
                case MAIN:
                    if (GUIFile.mainMenuEvents != null)
                        CommandUtil.executeCommands(player, (List<String>) GUIFile.mainMenuEvents.getList("close"));
                    break;
                case REWARD:
                    if (GUIFile.rewardMenuEvents != null)
                        CommandUtil.executeCommands(player, (List<String>) GUIFile.rewardMenuEvents.getList("close"));
                    break;
                default:
                    player.sendMessage(LangFile.prefix + "菜单点击事件执行错误！请将报错发送给管理员查看！");
            }
        }
    }
}
