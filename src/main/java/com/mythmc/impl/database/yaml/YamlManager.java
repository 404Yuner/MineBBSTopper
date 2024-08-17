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


    private File cooldownYaml = new File(MineBBSTopper.INSTANCE.getDataFolder(), "data/cooldown.yml");
    private File playerYaml = new File(MineBBSTopper.INSTANCE.getDataFolder(), "data/player.yml");
    private File rewardYaml = new File(MineBBSTopper.INSTANCE.getDataFolder(), "data/rewards.yml");

    private FileConfiguration cooldownConfig;
    private FileConfiguration playerConfig;
    private FileConfiguration rewardConfig;

    // 创建表格的方法
    public void createTable() {
        // 如果 "data/cooldown.yml" 文件不存在，则将其从资源中复制到文件系统
        saveDefaultConfigIfNotExists(cooldownYaml, "data/cooldown.yml");
        // 如果 "data/player.yml" 文件不存在，则将其从资源中复制到文件系统
        saveDefaultConfigIfNotExists(playerYaml, "data/player.yml");
        // 如果 "data/rewards.yml" 文件不存在，则将其从资源中复制到文件系统
        saveDefaultConfigIfNotExists(rewardYaml, "data/rewards.yml");

        // 加载 "cooldown.yml" 文件的配置
        cooldownConfig = YamlConfiguration.loadConfiguration(cooldownYaml);
        // 加载 "player.yml" 文件的配置
        playerConfig = YamlConfiguration.loadConfiguration(playerYaml);
        // 加载 "rewards.yml" 文件的配置
        rewardConfig = YamlConfiguration.loadConfiguration(rewardYaml);
    }

    // 保存默认配置文件的方法，如果文件不存在则从资源中复制
    private void saveDefaultConfigIfNotExists(File file, String resourcePath) {
        // 检查文件是否存在
        if (!file.exists()) {
            // 如果文件不存在，则从资源路径复制文件
            MineBBSTopper.INSTANCE.saveResource(resourcePath, false);
        }
    }
    // 保存配置文件的方法
    private void saveConfig(FileConfiguration config, File file) {
        try {
            // 将配置保存到文件
            config.save(file);
        } catch (IOException e) {
            // 处理文件保存时的异常
            e.printStackTrace();
        }
    }
    // 向玩家记录中插入新的记录时间
    public void insertPlayerData(String playerName, String newTime) {
        // 构造记录路径，格式为 "玩家名.record"
        String path = playerName + ".record";
        // 获取玩家记录的时间列表
        List<String> records = playerConfig.getStringList(path);
        // 添加新的记录时间
        records.add(newTime);
        // 更新玩家记录
        playerConfig.set(path, records);
        // 保存更新后的配置
        saveConfig(playerConfig, playerYaml);
    }

    // 计算指定玩家的记录数量
    public int countPlayerRecords(String playerName) {
        // 构造记录路径，格式为 "玩家名.record"
        String path = playerName + ".record";
        // 获取玩家记录的时间列表
        List<String> records = playerConfig.getStringList(path);
        // 返回记录数量
        return records.size();
    }

    // 获取前十名玩家的排行榜
    public List<String> getTopTenPlayers() {
        // 获取所有玩家及其记录数的映射
        Map<String, Integer> playerCounts = playerConfig.getKeys(false).stream()
                .collect(Collectors.toMap(
                        playerName -> playerName,
                        playerName -> playerConfig.getStringList(playerName + ".record").size()
                ));

        // 对玩家记录数进行排序并获取前 ConfigFile.rankPlayer 名
        List<Map.Entry<String, Integer>> topPlayers = playerCounts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(ConfigFile.rankPlayer)
                .collect(Collectors.toList());

        // 格式化排名信息并返回
        return IntStream.range(0, topPlayers.size())
                .mapToObj(index -> {
                    Map.Entry<String, Integer> entry = topPlayers.get(index);
                    return GUIFile.rankFormat
                            .replace("%player%", entry.getKey())
                            .replace("%count%", String.valueOf(entry.getValue()))
                            .replace("%rank%", String.valueOf(index + 1));
                })
                .collect(Collectors.toList());
    }

    // 获取冷却时间的数据
    public long getCooldownData() {
        // 如果配置中包含 "cooldown" 键，则返回其值，否则返回 0
        return cooldownConfig.contains("cooldown") ? cooldownConfig.getLong("cooldown") : 0;
    }

    // 设置冷却时间的数据
    public void setCooldownData(long newCooldownValue) {
        // 设置 "cooldown" 键的值
        cooldownConfig.set("cooldown", newCooldownValue);
        // 保存更新后的配置
        saveConfig(cooldownConfig, cooldownYaml);
        // 记录日志，显示成功设置的冷却时间
        Debugger.logger("成功设置YAML文件中下一次可顶贴的时间为 " + TimeUtil.convertTimestamp(newCooldownValue));
    }

    // 获取指定玩家的最新记录时间
    public String getLatestRecordTime(String playerName) {
        // 创建日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 如果玩家记录不存在，则返回 "暂无顶贴"
        if (!playerConfig.contains(playerName)) {
            return "暂无顶贴";
        }

        // 获取玩家记录时间列表，解析时间，并返回最新的时间格式化字符串
        return playerConfig.getStringList(playerName + ".record").stream()
                .filter(Objects::nonNull)
                .map(record -> {
                    try {
                        return dateFormat.parse(record);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .map(dateFormat::format)
                .orElse("暂无顶贴");
    }

    // 获取服务器中所有玩家的总记录数
    public int getServerRecord() {
        // 计算所有玩家记录时间列表的总和
        return playerConfig.getKeys(false).stream()
                .mapToInt(playerName -> playerConfig.getStringList(playerName + ".record").size())
                .sum();
    }

    // 检查玩家奖励数据是否存在
    public boolean playerRewardExists(String playerName) {
        // 检查 rewardConfig 中是否包含指定玩家的奖励数据
        return rewardConfig.contains(playerName);
    }

    // 检查玩家数据是否存在
    public boolean playerDataExists(String playerName) {
        // 检查 playerConfig 中是否包含指定玩家的数据
        return playerConfig.contains(playerName);
    }

    // 插入新的奖励数据到指定玩家的记录中
    public void insertRewardData(String playerName) {
        // 初始化奖励数据为 "0-0-0-0-0-0-0-0-0-0"
        rewardConfig.set(playerName, "0-0-0-0-0-0-0-0-0-0");
        // 保存更新后的配置
        saveConfig(rewardConfig, rewardYaml);
    }

    // 设置玩家的奖励数据
    public void setRewardData(String playerName, int label) {
        // 获取玩家的当前奖励数据并分割成数组
        String[] rewardArray = rewardConfig.getString(playerName, "0-0-0-0-0-0-0-0-0-0").split("-");

        // 如果标签在有效范围内，则将对应位置的奖励值设置为 "1"
        if (label >= 0 && label < rewardArray.length) {
            rewardArray[label] = "1";
        }

        // 将更新后的奖励数据拼接为字符串
        String updatedRewards = String.join("-", rewardArray);
        // 更新配置中的奖励数据
        rewardConfig.set(playerName, updatedRewards);
        // 保存更新后的配置
        saveConfig(rewardConfig, rewardYaml);
        // 记录日志，显示成功更新的奖励数据
        Debugger.logger("已将玩家 " + playerName + " 的 " + (label+1) + " 号累计奖励数据写入配置文件");
    }

    // 获取玩家的奖励数据并返回为字符串
    public String getRewardDataAsString(String playerName) {
        // 如果奖励数据存在，则返回奖励数据字符串，否则返回默认值
        return rewardConfig.contains(playerName) ? rewardConfig.getString(playerName) : "0-0-0-0-0-0-0-0-0-0";
    }

    // 获取玩家今天的记录数量
    public int getPlayerTodayRecords(String playerName) {
        // 获取当前日期，格式为 yyyy-MM-dd
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = LocalDate.now().format(dateFormatter);

        // 获取玩家记录时间列表，过滤出今天的记录，并计算数量
        return (int) playerConfig.getStringList(playerName + ".record").stream()
                .filter(record -> record.startsWith(todayDate)) // 过滤出今天的记录
                .count(); // 计算符合条件的记录数量
    }


}
