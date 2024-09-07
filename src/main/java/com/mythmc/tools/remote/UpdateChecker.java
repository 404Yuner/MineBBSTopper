package com.mythmc.tools.remote;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.LangFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final MineBBSTopper plugin;
    private static final String API_URL = "https://api.minebbs.com/api/openapi/v1/resources/8762"; // API 地址，获取更新信息的接口

    public UpdateChecker(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        checkForUpdates(null); // 适用于控制台情况
    }

    public void checkForUpdates(Player p) {
        if (ConfigFile.UpdateCheck) { // 如果配置文件中的更新检查选项被启用
            // 异步检查更新
            HandySchedulerUtil.runTaskAsynchronously(() -> {
                try {
                    // 获取JSON数据中的“data”部分
                    JSONObject json  = getJsonObject().getJSONObject("data");
                    String updateUrl = json.getString("view_url"); // 提取更新地址
                    String updateMessage = createUpdateMessage(plugin.getDescription().getVersion(),
                            json.getString("version").substring(0, 3), updateUrl); // 创建更新消息

                    if (p == null) {
                        // 如果没有指定玩家，记录更新信息到插件日志中
                        plugin.logger("§5更新 §8| " + updateMessage);
                    } else {
                        // 向指定玩家发送更新信息
                        p.sendMessage(LangFile.prefix + updateMessage + " §a感谢您的使用！");
                    }
                } catch (Exception e) {
                    // 捕获并记录检查更新时的异常
                    plugin.logger("§5更新 §8| §c顶贴插件检查更新时出现错误!");
                }
            });
        }
    }

    private String createUpdateMessage(String pluginVersion, String latestVersion, String updateUrl) {
        // 创建更新信息的消息字符串
        if (latestVersion != null) {
            return Double.parseDouble(pluginVersion) < Double.parseDouble(latestVersion) ?
                    // 当前版本低于最新版本时，生成新版本提示
                    "§d顶贴插件发现新版本! 当前版本: v" + pluginVersion + ", 最新版本: v" + latestVersion + "\n§6下载地址为: §a§n" + updateUrl:
                    // 当前版本已经是最新版本时，生成已更新消息
                    "§a您已使用最新版本: v" + pluginVersion;
        }
        // 发生错误时的消息
        return "§c顶贴插件检查更新时出现错误!";
    }

    private JSONObject getJsonObject() throws IOException {
        // 创建链接
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // 设置请求方法为GET

        // 读取响应
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) response.append(line); // 将每一行的响应数据追加到StringBuilder中
        in.close();

        // 解析JSON数据
        return new JSONObject(response.toString()); // 将响应数据转换为JSONObject并返回
    }
}
