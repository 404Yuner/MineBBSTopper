package com.mythmc.file.statics;

import com.mythmc.MineBBSTopper;
import com.mythmc.tools.utils.ColorUtil;
import eu.decentsoftware.holograms.api.utils.scheduler.S;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.PushbackInputStream;

public class HologramFile {
    public static String locationWorld;
    public static float locationX;
    public static float locationY;
    public static float locationZ;
    public static String hologramHeader;
    public static String hologramFooter;

    public static boolean hologramEnable;
    public  void load() {
        File hologramFile = new File(MineBBSTopper.INSTANCE.getDataFolder(), "data/hologram.yml");
        if (!hologramFile.exists()) MineBBSTopper.INSTANCE.saveResource("data/hologram.yml", false);
        YamlConfiguration hologramConfig = YamlConfiguration.loadConfiguration(hologramFile);
        hologramHeader = ColorUtil.colorize(hologramConfig.getString("Hologram.header"));
        hologramFooter = ColorUtil.colorize(hologramConfig.getString("Hologram.footer"));
        hologramEnable = hologramConfig.getBoolean("Hologram.enable");
        locationWorld = hologramConfig.getString("Hologram.location.world");
        locationX = (float) hologramConfig.getDouble("Hologram.location.x");
        locationY = (float) hologramConfig.getDouble("Hologram.location.y");
        locationZ = (float) hologramConfig.getDouble("Hologram.location.z");
    }
}
