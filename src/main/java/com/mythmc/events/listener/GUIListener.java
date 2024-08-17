package com.mythmc.events.listener;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.api.event.PlayerAccumulatedRewardClaimEvent;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.commands.gui.external.RewardSlotData;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.CommandUtil;
import com.mythmc.tools.utils.MessageUtil;
import com.mythmc.tools.utils.PAPIUtil;
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

    // 存储每个玩家最后一次点击事件的时间
    public static Map<String, Long> lastClickTimes = new HashMap<>();

    // 处理菜单点击事件
    @EventHandler
    public void onMainGUIClick(InventoryClickEvent event) {
        // 检查事件的发起者是否是玩家
        if (!(event.getWhoClicked() instanceof Player)) {
            return; // 如果不是玩家，则忽略该事件
        }

        Player player = (Player) event.getWhoClicked();

        // 处理点击事件，首先检查玩家点击的界面标题
        if (event.getView().getTitle().equals(PAPIUtil.set(player, GUIFile.mainMenuName))) {
            // 取消事件
            event.setCancelled(true);
            // 检查点击时间间隔的通用方法
            if (isClickTooFast(player)) {
                return;
            }

            // 获取点击的槽位
            int rawSlot = event.getRawSlot();
            // 获取该槽位对应的命令列表
            List<String> commands = null;
            for (Map.Entry<List<Integer>, List<String>> entry : MainGUI.slotCommands.entrySet()) {
                List<Integer> slots = entry.getKey();
                if (slots.contains(rawSlot)) {
                    // 如果找到了包含该槽位的List<Integer>键，获取对应的命令列表
                    commands = entry.getValue();
                    // 跳出循环，因为我们已经找到了对应的命令列表
                    break;
                }
            }
            if (commands != null) {
                // 执行命令列表中的命令
                CommandUtil.executeCommands(player, commands);
            }
        }
    }
    @EventHandler
    public void onRewardGUIClick(InventoryClickEvent event) {
        // 如果点击者不是玩家，直接返回
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // 将点击者转换为玩家对象
        Player player = (Player) event.getWhoClicked();

        // 检查点击的物品是否在指定的 GUI 界面中
        if (event.getView().getTitle().equals(PAPIUtil.set(player, GUIFile.rewardMenuName))) {
            // 取消默认的点击行为
            event.setCancelled(true);
            // 检查点击时间间隔的通用方法
            if (isClickTooFast(player)) {
                return;
            }

            String playerName = player.getName();
            // 获取点击位置的原始槽位编号
            int rawSlot = event.getRawSlot();
            // 根据槽位编号获取 SlotData 对象
            RewardSlotData rewardSlotData = null;
            for (Map.Entry<List<Integer>, RewardSlotData> entry : RewardGUI.slotData.entrySet()) {
                List<Integer> slots = entry.getKey();
                // 如果 rawSlot 在 slots 列表中
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
    }
    private boolean isClickTooFast(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastClickTime = lastClickTimes.getOrDefault(player.getName(), 0L);
        if (currentTime - lastClickTime < ConfigFile.clickInterval * 1000L) {
            MessageUtil.sendMessage(player, "§c你点太快了，请等待 " + ConfigFile.clickInterval + " 秒");
            return true;
        }
        lastClickTimes.put(player.getName(), currentTime);
        return false;
    }

    /**
     * 下面是兼容低版本插件的笨蛋写法，大佬们勿喷
     *
     * @param e 菜单打开事件
     */
    @EventHandler
    public void onGUIOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        // 处理点击事件
        if (e.getView().getTitle().equals(PAPIUtil.set(player, GUIFile.mainMenuName))) {
            if (GUIFile.mainMenuEvents != null) 
                CommandUtil.executeCommands(player, (List<String>) GUIFile.mainMenuEvents.getList("open"));
        }
        if (e.getView().getTitle().equals(PAPIUtil.set(player, GUIFile.rewardMenuName))) {
            if (GUIFile.rewardMenuEvents != null)
                CommandUtil.executeCommands(player, (List<String>) GUIFile.rewardMenuEvents.getList("open"));
        }
    }
    @EventHandler
    public void onGUIClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        // 处理点击事件
        if (e.getView().getTitle().equals(PAPIUtil.set(player, GUIFile.mainMenuName))) {
            if (GUIFile.mainMenuEvents != null)
                CommandUtil.executeCommands(player, (List<String>) GUIFile.mainMenuEvents.getList("close"));
        }
        if (e.getView().getTitle().equals(PAPIUtil.set(player, GUIFile.rewardMenuName))) {
            if (GUIFile.rewardMenuEvents != null)
                CommandUtil.executeCommands(player, (List<String>) GUIFile.rewardMenuEvents.getList("close"));
        }
    }
}
