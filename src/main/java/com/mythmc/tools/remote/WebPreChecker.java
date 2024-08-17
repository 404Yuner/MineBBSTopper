package com.mythmc.tools.remote;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.tools.remote.core.MineBBSHtmlParser;
import org.bukkit.Bukkit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class WebPreChecker {
    private final MineBBSTopper plugin;

    public WebPreChecker(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    // 启动预检查流程
    public void load() {
        try {
            // 分割配置文件中的重连设置
            String[] split = ConfigFile.reconnect.split(":");
            // 检查分割后的结果是否为两个部分
            if (split.length != 2)
                // 抛出异常并输出错误信息
                throw new IllegalArgumentException("§2检查 §8| §c配置格式错误，应该为 '最大重连次数:重连间隔秒数'");

            // 解析最大重连次数
            final int maxRetries = Integer.parseInt(split[0]);
            // 解析重连间隔秒数并转换为 ticks（20 ticks = 1秒）
            int reconnectInterval = Integer.parseInt(split[1]) * 20;
            // 执行带重试机制的预检查
            preCheckWithRetries(maxRetries, 0, reconnectInterval);
        } catch (NumberFormatException e) {
            // 捕捉到数字格式异常，输出日志信息
            plugin.logger("§2检查 §8| §c配置的整数格式不正确，请检查是否为 '最大重连次数:重连间隔秒数'");
        } catch (IllegalArgumentException e) {
            // 捕捉到非法参数异常，输出日志信息
            plugin.logger("§2检查 §8| §c配置的格式不正确，请检查是否为 '最大重连次数:重连间隔秒数'");
        } catch (Exception e) {
            // 捕捉到其他异常，输出日志信息并打印堆栈跟踪
            plugin.logger("§2检查 §8| §c预检查时发生未知异常");
            e.printStackTrace();
        }
    }

    // 带重试机制的预检查方法
    private void preCheckWithRetries(int maxRetries, int currentRetry, int reconnectInterval) {
        // 异步执行预检查
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            try {
                // 发起 HTTP 请求
                Connection.Response response = Jsoup.connect(ConfigFile.url)
                        .userAgent(MineBBSTopper.AGENT)
                        .timeout(10000)
                        .execute();

                // 获取响应状态码
                int statusCode = response.statusCode();

                // 如果状态码为 200，表示成功
                if (statusCode == 200) {
                    plugin.logger("§2检查 §8| §a网址预检查成功，成功加载 MineBBSTopper，感谢您的使用!");
                    // 异步获取 HTML 中的时间元素
                    MineBBSHtmlParser.fetchTimeElementsAsync(is -> {});
                } else {
                    // 如果状态码不为 200，表示失败，记录日志并尝试重连
                    plugin.logger("§2检查 §8| §c网址预检查失败，尝试重新连接，网址状态码: " + statusCode);
                    if (currentRetry < maxRetries - 1) {
                        // 延时后重试
                        HandySchedulerUtil.runTaskLater(() ->
                                preCheckWithRetries(maxRetries, currentRetry + 1, reconnectInterval), reconnectInterval);
                    } else {
                        // 达到最大重试次数，禁用插件
                        plugin.logger("§2警告 §8| §c达到最大重试次数，插件已禁用！");
                        plugin.getServer().getPluginManager().disablePlugin(plugin);
                    }
                }
            } catch (IOException e) {
                // 捕捉到 I/O 异常，记录日志并尝试重连
                plugin.logger("§2检查 §8| §c网址预检查失败，尝试重新连接，请检查网址是否无误或帖子状态是否正常！");
                e.getCause();
                if (currentRetry < maxRetries - 1) {
                    // 延时后重试
                    HandySchedulerUtil.runTaskLater(() ->
                            preCheckWithRetries(maxRetries, currentRetry + 1, reconnectInterval), reconnectInterval);
                } else {
                    // 达到最大重试次数，禁用插件
                    plugin.logger("§2警告 §8| §c达到最大重试次数，插件已禁用！");
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
            }
        });
    }
}