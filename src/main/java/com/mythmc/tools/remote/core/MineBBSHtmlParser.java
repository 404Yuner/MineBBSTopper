package com.mythmc.tools.remote.core;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import cn.handyplus.lib.adapter.WorldSchedulerUtil;
import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;

public class MineBBSHtmlParser {

    // 异步获取时间元素的方法

    public static void fetchTimeElementsAsync(TimeElementResultHandler handler) {
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            try {
                boolean found = false;
                boolean canClaim = false;
                int pageNumber = 1; // 从第1页开始
                int retryCount = 3; // 设置重试次数

                while (true) {
                    Connection connection = Jsoup.connect("https://www.minebbs.com/servers/page-" + pageNumber)
                            .userAgent(MineBBSTopper.AGENT)
                            .timeout(10000); // 设置为10秒

                    if (ConfigFile.isProxyMode) connection.proxy(ConfigFile.proxyIP, ConfigFile.proxyPort);

                    boolean pageProcessed = false; // 标记当前页是否处理完成
                    for (int attempt = 0; attempt < retryCount; attempt++) {
                        try {
                            Document doc = connection.get();
                            Elements elements = doc.select(".structItem-cell--latest a");

                            for (Element element : elements) {
                                String href = element.attr("href");
                                String dataTime = element.select("time").attr("data-time");
                                if (!dataTime.trim().isEmpty()) {
                                    if (ConfigFile.url.contains(href.replace("/latest", ""))) {
                                        long timeStamp = Long.parseLong(dataTime);
                                        Debugger.logger("§a从宣传主站上提取到的顶贴时间为 §e" + TimeUtil.convertTimestamp(timeStamp));

                                        // 当前时间 < 获取的时间 + 领取时间限制
                                        if (System.currentTimeMillis() / 1000L < timeStamp + ConfigFile.claimTime * 60L) {
                                            canClaim = true;
                                        }
                                        found = true;
                                        pageProcessed = true; // 数据已处理完成
                                        break;
                                    }
                                }
                            }

                            if (found) {
                                break; // 找到数据后退出重试循环
                            } else {
                                // 添加延迟避免被网站禁止访问
                                Thread.sleep(1000); // 等待1秒
                                pageNumber++; // 翻到下一页
                                // 边界检查
                                if (pageNumber >= 10) {
                                    Debugger.logger("§c你的宣传贴上次顶贴时间距今太为久远，请顶贴后再重新尝试获取");
                                    pageProcessed = true; // 页码超限，处理结束
                                    break;
                                }
                            }
                        } catch (SocketTimeoutException e) {
                            if (attempt == retryCount - 1) {
                                Debugger.logger("§c网络波动！请求超时，已达到最大重试次数。重试即可！");
                                handler.handleResult("timeout");
                                return; // 超过尝试次数后返回
                            }
                            Debugger.logger("§e请求超时，正在重试第 " + (attempt + 1) + " 次...");
                        } catch (Exception e) {
                            handler.handleResult("error");
                            Debugger.logger("§c获取异常，请寻求插件开发者帮助");
                            e.printStackTrace();
                            return;
                        }
                    }

                    if (pageProcessed) {
                        break; // 退出 while 循环
                    }
                }

                // 返回结果
                handler.handleResult(canClaim ? "true" : "false");
            } catch (Exception e) {
                handler.handleResult("error");
                Debugger.logger("§c获取异常，请寻求插件开发者帮助");
                e.printStackTrace();
            }
        });
    }
}