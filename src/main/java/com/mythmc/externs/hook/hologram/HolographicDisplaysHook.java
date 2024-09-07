package com.mythmc.externs.hook.hologram;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.externs.hook.placeholders.PlaceholderCache;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.HologramFile;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class HolographicDisplaysHook {
    private static Hologram hologram = null;
    public static boolean isHolographicDisplaysEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public void load() {
        if (isHolographicDisplaysEnable() && HologramFile.hologramEnable) {
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
        if (hologram != null) {
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
        hologram = HolographicDisplaysAPI.get(MineBBSTopper.INSTANCE).createHologram(location);

        finalList.forEach(line -> hologram.getLines().appendText(line));
    }

    public static void removeHologram() {
        if (isHolographicDisplaysEnable() && HologramFile.hologramEnable) {
            // 清空缓存
            if (hologram != null)
                hologram.delete();
        }
    }
}
