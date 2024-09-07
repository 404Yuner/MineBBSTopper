package com.mythmc.tools.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String colorize(String inputString) {
        // 替换 &xRGB 颜色
        inputString = ChatColor.translateAlternateColorCodes('&', inputString);

        // 替换十六进制颜色
        Matcher matcher = HEX_PATTERN.matcher(inputString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group();
            String rgb = hexToRGB(hex);
            String minecraftColor = "§x" + rgb;
            matcher.appendReplacement(sb, minecraftColor);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
    public static List<String> colorize(List<String> inputList) {
        List<String> colorizedList = new ArrayList<>();
        for (String inputString : inputList) {
            colorizedList.add(colorize(inputString));
        }
        return colorizedList;
    }
    // 将十六进制颜色转换为 Minecraft 支持的格式
    private static String hexToRGB(String hex) {
        // 移除前缀 #
        hex = hex.substring(1);

        // 将十六进制颜色转换为 Minecraft 颜色代码格式
        return String.format("§%s§%s§%s", hex.charAt(0), hex.charAt(1), hex.charAt(2)) +
                String.format("§%s§%s§%s", hex.charAt(3), hex.charAt(4), hex.charAt(5));
    }
}