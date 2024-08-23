package com.mythmc.tools.utils;

import com.mythmc.externs.HookManager;
import com.mythmc.externs.hook.placeholders.PlaceholderHook;
import org.bukkit.entity.Player;

import java.util.List;

public class PAPIUtil {
    public static String set(Player player, String input) {
        return HookManager.isPlaceholderHook ? PlaceholderHook.set(player,input) : input;
    }
    public static List<String> set(Player player, List<String> input) {
        return HookManager.isPlaceholderHook ? PlaceholderHook.set(player,input) : input;
    }
}
