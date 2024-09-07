package com.mythmc.file.statics;

import com.mythmc.MineBBSTopper;
import com.mythmc.tools.utils.ColorUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GUIFile {
    public static String mainMenuName,rankFormat;
    public static int mainMenuSize;
    public static ConfigurationSection mainMenuItems,mainMenuEvents;

    // 累计GUI
    public static ConfigurationSection rewardMenuItems,rewardMenuEvents;
    public static String rewardMenuName,rewardMenuStatusYes,rewardMenuStatusNo,rewardMenuStatusClaim,rewardMenuClaimedMat;
    public static int rewardMenuSize;
    public static boolean rewardMenuEnable;
    public static List<String> mainMenuLayout,rewardMenuLayout = new ArrayList<>();
    public void load(MineBBSTopper plugin){
        File guiFile = new File(plugin.getDataFolder(), "gui/main.yml");
        File rewardFile = new File(plugin.getDataFolder(), "gui/reward.yml");
        if (!guiFile.exists()) plugin.saveResource("gui/main.yml", false);
        if (!rewardFile.exists()) plugin.saveResource("gui/reward.yml", false);

        YamlConfiguration mainMenu = YamlConfiguration.loadConfiguration(guiFile);
        YamlConfiguration rewardMenu = YamlConfiguration.loadConfiguration(rewardFile);
        // 主菜单的配置
        mainMenuName = ColorUtil.colorize(mainMenu.getString("Menu.title"));
        mainMenuSize = mainMenu.getInt("Menu.size");
        mainMenuItems = mainMenu.getConfigurationSection("Menu.items");
        mainMenuEvents = mainMenu.getConfigurationSection("Menu.events");
        rankFormat = ColorUtil.colorize(mainMenu.getString("Menu.rankFormat"));

        // 累计奖励菜单配置
        rewardMenuItems = rewardMenu.getConfigurationSection("Menu.items");
        rewardMenuEvents = rewardMenu.getConfigurationSection("Menu.events");
        rewardMenuName = ColorUtil.colorize(rewardMenu.getString("Menu.title"));
        rewardMenuSize = rewardMenu.getInt("Menu.size");
        rewardMenuEnable = rewardMenu.getBoolean("Enable",true);
        rewardMenuStatusYes = ColorUtil.colorize(rewardMenu.getString("Status.yes"));
        rewardMenuStatusNo = ColorUtil.colorize(rewardMenu.getString("Status.no"));
        rewardMenuStatusClaim = ColorUtil.colorize(rewardMenu.getString("Status.claimed"));
        rewardMenuClaimedMat = rewardMenu.getString("Menu.claimedMat");
        mainMenuLayout = (List<String>) mainMenu.getList("Menu.layout");
        rewardMenuLayout = (List<String>) rewardMenu.getList("Menu.layout");



    }
    public void clear() {
        mainMenuItems = null;
        rewardMenuItems = null;
    }
    
}
