package com.mythmc.tools.utils;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.impl.cache.TargetManager;

import java.util.Calendar;

public class RefreshUtil {

    private final MineBBSTopper plugin;

    public RefreshUtil(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    public void scheduleDailyTask() {
        // 计算下一个0点的时间戳
        long delay = calculateInitialDelay();
        long period = 24 * 60 * 60 * 20L; // 24小时，以20 ticks为单位

        // 使用调度器来执行任务
        HandySchedulerUtil.runTaskTimer(this::refreshData,delay,period);
    }

    private long calculateInitialDelay() {
        // 计算到24点的时间
        Calendar now = Calendar.getInstance();
        Calendar nextMidnight = (Calendar) now.clone();
        nextMidnight.set(Calendar.HOUR_OF_DAY, 0);
        nextMidnight.set(Calendar.MINUTE, 0);
        nextMidnight.set(Calendar.SECOND, 0);
        nextMidnight.add(Calendar.DAY_OF_MONTH, 1);

        long currentTime = now.getTimeInMillis();
        long nextMidnightTime = nextMidnight.getTimeInMillis();

        return (nextMidnightTime - currentTime) / 50; // 转换为 ticks
    }

    private void refreshData() {
        // 刷新每日玩家顶贴记录
        TargetManager.clearPlayerInfoCache();
        plugin.logger("§a任务 §8| §6每天0点执行的刷新任务已经运行，玩家当日顶贴次数已重置");
    }
}
