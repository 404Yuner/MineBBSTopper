package com.mythmc.events.listener;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.database.DatabaseManager;
import com.mythmc.tools.remote.UpdateChecker;
import com.mythmc.tools.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onOpJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        // 使用 Bukkit 调度器延迟 1 秒执行，避免被其他插件消息刷屏
        HandySchedulerUtil.runTaskLater( () -> {
            // 检查是否为 OP 并且是否需要更新检查
            if (p.isOp() && ConfigFile.UpdateCheck) new UpdateChecker(MineBBSTopper.INSTANCE).checkForUpdates(p);
            // 检查数据库是否存在玩家奖励数据，如果没有则插入
            if (!DatabaseManager.getDbManager().playerRewardExists(name)) DatabaseManager.getDbManager().insertRewardData(name);
            // 发送提醒消息（如果配置要求）
            if (ConfigFile.sendRemindMessage && !DatabaseManager.getDbManager().playerDataExists(name)) MessageUtil.sendMessage(p, LangFile.remindMessage);
        }, 20L); // 延迟 20 ticks (1 秒)
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String playerName = e.getPlayer().getName();

        // 清理玩家的所有缓存
        TargetManager.removePlayerInfo(playerName);
        MainGUI.cachedMainGUI.remove(playerName);
        RewardGUI.cachedRewardGUI.remove(playerName);
    }

}
