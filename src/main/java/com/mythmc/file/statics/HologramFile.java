package com.mythmc.file.statics;

import com.mythmc.MineBBSTopper;
import com.mythmc.tools.utils.ColorUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class HologramFile {
    public static String locationWorld;
    public static float locationX;
    public static float locationY;
    public static float locationZ;
    public static String hologramHeader;
    public static String hologramFooter;
    public static String hologramType;
    public static boolean hologramEnable;
    public  void load(MineBBSTopper plugin) {
        File hologramFile = new File(plugin.getDataFolder(), "data/hologram.yml");
        if (!hologramFile.exists()) plugin.saveResource("data/hologram.yml", false);
        YamlConfiguration hologramConfig = YamlConfiguration.loadConfiguration(hologramFile);
        hologramHeader = ColorUtil.colorize(hologramConfig.getString("Hologram.header"));
        hologramFooter = ColorUtil.colorize(hologramConfig.getString("Hologram.footer"));
        hologramEnable = hologramConfig.getBoolean("Hologram.enable");
        hologramType = hologramConfig.getString("Hologram.plugin");
        locationWorld = hologramConfig.getString("Hologram.location.world");
        locationX = (float) hologramConfig.getDouble("Hologram.location.x");
        locationY = (float) hologramConfig.getDouble("Hologram.location.y");
        locationZ = (float) hologramConfig.getDouble("Hologram.location.z");
    }
}
