package com.mythmc.tools.utils;

import com.mythmc.file.statics.LangFile;
import org.bukkit.entity.Player;

public class MessageUtil {
    public static void sendMessage(Player player,String meg) {
        player.sendMessage(LangFile.prefix + meg);
    }
}
