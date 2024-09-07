package com.mythmc.externs.hook.hologram;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.externs.hook.placeholders.PlaceholderCache;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.HologramFile;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class DecentHologramsHook {

    public static boolean isDecentHologramsEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    }

    public void load() {
        if (isDecentHologramsEnable() && HologramFile.hologramEnable) {
            refreshSchedule();
        }
    }

    private void refreshSchedule() {
        HandySchedulerUtil.runTaskTimer(this::refresh, 0, ConfigFile.rankRefreshInterval * 20L);
    }

    private void refresh() {
        Location location = new Location(Bukkit.getWorld(HologramFile.locationWorld), HologramFile.locationX, HologramFile.locationY, HologramFile.locationZ);
        // 执行 delete 方法
        removeHologram();
        // 执行 create 方法
        createHologram(location, PlaceholderCache.getCache());
    }

    public static void createHologram(Location location, List<String> originalList) {
        // 检查是否已经创建了名为 "minebbstopper_rank" 的 hologram
        if (DHAPI.getHologram("minebbstopper_rank") != null) {
            return;
        }

        // 复制原始列表以避免修改原始数据
        List<String> list = new ArrayList<>(originalList);

        // 处理列表数据，添加页眉和页脚
        List<String> finalList = new ArrayList<>();
        finalList.add(HologramFile.hologramHeader); // 页眉

        finalList.addAll(list); // 添加列表内容

        finalList.add(HologramFile.hologramFooter); // 页脚

        // 创建并配置 Hologram
        DHAPI.createHologram("minebbstopper_rank", location, finalList);

    }

    public static void removeHologram() {
        if (isDecentHologramsEnable() && HologramFile.hologramEnable) {
            // 清空缓存
            Hologram hologram = DHAPI.getHologram("minebbstopper_rank");
            if (hologram != null)
                hologram.delete();
        }
    }

}
