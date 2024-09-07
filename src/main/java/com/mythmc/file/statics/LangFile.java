package com.mythmc.file.statics;

import com.mythmc.MineBBSTopper;
import com.mythmc.tools.utils.ColorUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LangFile {
    public static String prefix,failMsg,needPermissionMsg,waitMsg,openIntervalMsg,upIntervalMsg,networkError,rewardUntenable,rewardClaimNo,rewardClaimYes,rewardClaimed,processing,limited,remindMessage,timeoutMsg;
    public void load(MineBBSTopper plugin) {
        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) plugin.saveResource("lang.yml", false);

        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
        prefix = ColorUtil.colorize(lang.getString("Message.prefix"));
        failMsg = ColorUtil.colorize(lang.getString("Message.fail"));
        needPermissionMsg = ColorUtil.colorize(lang.getString("Message.needPermission"));
        waitMsg = ColorUtil.colorize(lang.getString("Message.wait"));
        openIntervalMsg = ColorUtil.colorize(lang.getString("Message.openIntervalMsg"));
        upIntervalMsg = ColorUtil.colorize(lang.getString("Message.upIntervalMsg"));
        networkError = ColorUtil.colorize(lang.getString("Message.networkError"));
        rewardUntenable = ColorUtil.colorize(lang.getString("Message.rewardUntenable"));
        rewardClaimNo = ColorUtil.colorize(lang.getString("Message.rewardClaimNo"));
        rewardClaimYes = ColorUtil.colorize(lang.getString("Message.rewardClaimYes"));
        rewardClaimed = ColorUtil.colorize(lang.getString("Message.rewardClaimed"));
        processing = ColorUtil.colorize(lang.getString("Message.processing", "&c当前已有顶贴检测正在进行中，本次检查顶贴失败，请过一会儿再试！"));
        limited = ColorUtil.colorize(lang.getString("Message.limited", "&c每天仅可顶贴%limit%次，你目前已经达到限制了！"));
        remindMessage =ColorUtil.colorize(lang.getString("Message.remindMessage","&a快来顶贴服务器，获得丰厚奖励！详情指令 &c&n/minebbstopper open"));
        timeoutMsg = ColorUtil.colorize(lang.getString("Message.timeout","&c服务器网络波动，获取顶贴失败！请稍后重试。"));
    }


}
