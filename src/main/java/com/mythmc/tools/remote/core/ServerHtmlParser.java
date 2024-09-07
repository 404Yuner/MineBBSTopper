//package com.mythmc.tools.remote.core;
//
//import com.mythmc.MineBBSTopper;
//import com.mythmc.file.statics.ConfigFile;
//import com.mythmc.tools.Debugger;
//import com.mythmc.tools.remote.core.parser.ParserManager;
//import com.mythmc.tools.utils.TimeUtil;
//import org.bukkit.Bukkit;
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//public class ServerHtmlParser implements ParserManager {
//
//    // 异步获取时间元素的方法
//    public void fetchTimeElementsAsync(TimeElementResultHandler handler) {
//        // 使用 Bukkit 的异步任务调度器执行任务
//        runTaskAsynchronously(MineBBSTopper.INSTANCE, () -> {
//            try {
//                // 使用 Jsoup 连接到指定的 URL
//                Connection connection = Jsoup.connect(ConfigFile.url)
//                        .userAgent(MineBBSTopper.AGENT) // 设置用户代理
//                        .timeout(10000); // 设置超时时间为10秒
//                if (ConfigFile.isProxyMode) connection.proxy(ConfigFile.proxyIP, ConfigFile.proxyPort); // 使用代理
//                Document doc = connection.get(); // 获取网页内容
//                Elements timeElements = doc.select("time.u-dt"); // 选择网页上所有具有 'time.u-dt' 类的元素
//
//                // 使用 Stream API 处理时间元素
//                // 处理前两个元素
//                List<String> timeList = timeElements.stream().limit(2) // 转换为 Element 对象
//                        .map(timeElement -> timeElement.attr("data-time")) // 提取 'data-time' 属性的值
//                        .collect(Collectors.toList()); // 收集到列表中
//
//                if (timeList.size() >= 2) { // 如果找到至少两个时间元素
//                    long timeStamp = Long.parseLong(timeList.get(1)); // 从列表中获取第二个时间戳
//                    // 将时间戳转换为格式化时间字符串，输出提取到的时间到控制台
//                    Debugger.logger("§a从宣传贴上提取到的顶贴时间为 §e" + TimeUtil.convertTimestamp(timeStamp));
//                    long currentTimestamp = System.currentTimeMillis() / 1000; // 获取当前时间戳（秒）
//
//                    long compare = currentTimestamp - timeStamp; // 计算时间差
//
//                    // 检查时间差是否大于配置文件中的阈值（以分钟为单位）
//                    boolean isMoreThanTenMinutes = compare / 60 <= ConfigFile.claimTime;
//                    // 调用回调方法处理结果，返回布尔值的字符串格式
//                    handler.handleResult(String.valueOf(isMoreThanTenMinutes));
//                } else {
//                    // 没有找到时间元素，调用回调方法
//                    handler.handleResult("error");
//                    Debugger.logger("§c没有找到对应元素，返回为空，请检查宣传帖子状态!!!");
//                }
//            } catch (IOException e) {
//                // 捕获并处理 IO 异常
//                Debugger.logger("§c获取异常，请寻求插件开发者帮助: " + e.getMessage());
//                // 返回异常表示
//                handler.handleResult("error");
//            }
//        });
//    }
//}