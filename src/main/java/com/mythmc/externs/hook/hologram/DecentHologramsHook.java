package com.mythmc.externs.hook.hologram;

import com.mythmc.MineBBSTopper;
import com.mythmc.externs.hook.placeholders.PlaceholderCache;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.HologramFile;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DecentHologramsHook {
    public static boolean isDecentHologramsHooked = false;

    public static boolean setupDecentHolograms() {
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            isDecentHologramsHooked = true;
            return true;
        }
        return false;
    }

    public void load() {
        if (setupDecentHolograms() && HologramFile.hologramEnable) {
            refreshSchedule();
        }
    }

    private void refreshSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 设置位置
                Location location = new Location(Bukkit.getWorld(HologramFile.locationWorld), HologramFile.locationX, HologramFile.locationY, HologramFile.locationZ);

                // 执行 delete 方法
                removeHologram();

                // 执行 create 方法
                createHologram(location, PlaceholderCache.getCache());
            }
        }.runTaskTimer(MineBBSTopper.INSTANCE, 0, ConfigFile.rankRefreshInterval * 20L);
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
        if (setupDecentHolograms() && HologramFile.hologramEnable) {
            // 清空缓存
            Hologram hologram = DHAPI.getHologram("minebbstopper_rank");
            if (hologram != null)
                hologram.delete();
        }
    }

}
