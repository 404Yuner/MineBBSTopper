package com.mythmc.impl.database.yaml;

import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.api.DbManager;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.TimeUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class YamlManager implements DbManager {

    private File cooldownYaml = new File(MineBBSTopper.instance.getDataFolder(), "data/cooldown.yml");
    private File playerYaml = new File(MineBBSTopper.instance.getDataFolder(), "data/player.yml");
    private File rewardYaml = new File(MineBBSTopper.instance.getDataFolder(), "data/rewards.yml");

    private FileConfiguration cooldownConfig;

    private FileConfiguration playerConfig;
    private FileConfiguration rewardConfig;

    public void createTable() {

        if (!cooldownYaml.exists()) MineBBSTopper.instance.saveResource("data/cooldown.yml", false);
        if (!playerYaml.exists()) MineBBSTopper.instance.saveResource("data/player.yml", false);
        if (!rewardYaml.exists()) MineBBSTopper.instance.saveResource("data/rewards.yml", false);

        cooldownConfig = YamlConfiguration.loadConfiguration(cooldownYaml);
        playerConfig = YamlConfiguration.loadConfiguration(playerYaml);
        rewardConfig = YamlConfiguration.loadConfiguration(rewardYaml);
    }


    public void insertPlayerData(String playerName, String newTime) {
        // 获取或创建玩家记录
        String path = playerName + ".record";
        List<String> records = playerConfig.getStringList(path);

        // 添加新的时间记录
        records.add(newTime);

        // 更新配置文件
        playerConfig.set(path, records);
        try {
            playerConfig.save(playerYaml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int countPlayerRecords(String playerName) {
        // 获取玩家的记录路径
        String path = playerName + ".record";

        // 获取记录列表
        List<String> records = playerConfig.getStringList(path);

        // 返回记录的数量
        return records.size();
    }

    public List<String> getTopTenPlayers() {
        // 计算每个玩家的记录数量，并将结果存入一个映射中
        Map<String, Integer> playerCounts = playerConfig.getKeys(false).stream()
                .collect(Collectors.toMap(
                        playerName -> playerName, // 玩家名称作为键
                        playerName -> playerConfig.getStringList(playerName + ".record").size() // 玩家记录数量作为值
                ));

        // 提取前十名玩家，并格式化为字符串列表
        List<Map.Entry<String, Integer>> topPlayers = playerCounts.entrySet().stream() // 将映射的条目转化为流
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) // 按记录数量降序排序
                .limit(ConfigFile.rankPlayer) // 取设置的排名
                .collect(Collectors.toList()); // 收集为列表

        // 格式化前十名玩家的字符串列表，包含排名信息
        return IntStream.range(0, topPlayers.size())
                .mapToObj(index -> {
                    Map.Entry<String, Integer> entry = topPlayers.get(index);
                    return GUIFile.rankFormat
                            .replace("%player%", entry.getKey()) // 替换玩家名称占位符
                            .replace("%count%", String.valueOf(entry.getValue())) // 替换记录数量占位符
                            .replace("%rank%", String.valueOf(index + 1)); // 替换排名占位符
                })
                .collect(Collectors.toList()); // 将结果收集为列表
    }

    public long getCooldownData() {
        return cooldownConfig.contains("cooldown") ? cooldownConfig.getLong("cooldown") : 0;
    }

    public void setCooldownData(long newCooldownValue) {
        cooldownConfig.set("cooldown", newCooldownValue);
        try {
            cooldownConfig.save(cooldownYaml);
            Debugger.logger("成功设置YAML文件中下一次可顶贴的时间为 " + TimeUtil.convertTimestamp(newCooldownValue));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLatestRecordTime(String playerName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 检查玩家记录是否存在
        if (!playerConfig.contains(playerName)) {
            return "暂无顶贴";
        }

        // 获取玩家记录时间列表，并找到最新的记录时间
        return playerConfig.getStringList(playerName + ".record").stream()
                .filter(Objects::nonNull) // 过滤掉 null 值
                .map(record -> {
                    try {
                        return dateFormat.parse(record); // 尝试解析时间
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull) // 过滤掉解析失败的记录
                .max(Date::compareTo) // 找到最新的时间
                .map(dateFormat::format) // 格式化为字符串
                .orElse("暂无顶贴"); // 如果没有最新时间，返回默认消息
    }
    public int getServerRecord() {
        return playerConfig.getKeys(false).stream() // 获取所有玩家名称并转化为流
                .mapToInt(playerName -> playerConfig.getStringList(playerName + ".record").size()) // 获取每个玩家的记录数量并转化为 int 流
                .sum(); // 对所有记录数量求和
    }

    // 配置奖励的方法
    public boolean playerRewardExists(String playerName) {
        return rewardConfig.contains(playerName);
    }
    public boolean playerDataExists(String playerName) {
        return playerConfig.contains(playerName);
    }
    public void insertRewardData(String playerName) {
        rewardConfig.set(playerName, "0-0-0-0-0-0-0-0-0-0");
        try {
            rewardConfig.save(rewardYaml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRewardData(String playerName, int label) {
        // 从配置文件中获取玩家的奖励数据，默认值为全 0
        String[] rewardArray = rewardConfig.getString(playerName, "0-0-0-0-0-0-0-0-0-0").split("-");

        // 确保 label 在数组范围内
        if (label >= 0 && label < rewardArray.length) {
            // 更新指定的奖励数据
            rewardArray[label] = "1";
        }

        // 将更新后的奖励数据设置回配置文件
        String updatedRewards = String.join("-", rewardArray);
        rewardConfig.set(playerName, updatedRewards);

        try {
            rewardConfig.save(rewardYaml); // 保存到文件
            Debugger.logger("已将玩家 " + playerName + " 的 " + label + " 号累计奖励数据写入配置文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRewardDataAsString(String playerName) {
        return rewardConfig.contains(playerName) ? rewardConfig.getString(playerName) : "0-0-0-0-0-0-0-0-0-0";
    }

    public int getPlayerTodayRecords(String playerName) {
        // 获取当前日期，格式为 yyyy-MM-dd
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = LocalDate.now().format(dateFormatter);

        // 获取玩家记录时间列表，并使用 Stream API 进行处理
        return (int) playerConfig.getStringList(playerName + ".record").stream()
                .filter(record -> record.startsWith(todayDate)) // 过滤出今天的记录
                .count(); // 计算符合条件的记录数量
    }

}
