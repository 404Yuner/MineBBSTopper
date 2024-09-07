package com.mythmc.api; // 定义包名，表明这个接口属于 com.mythmc.api 包

import eu.decentsoftware.holograms.api.utils.scheduler.S;

import java.util.List; // 导入 List 类，以便在接口中使用列表

public interface DbManager { // 定义一个公开的接口 DbManager

    // 创建数据库表
    void createTable();

    // 计算指定玩家的记录数
    int countPlayerRecords(String playerName);

    // 插入玩家数据，包括玩家名称和当前时间
    void insertPlayerData(String playerName, String currentTime);

    // 获取排名前十的玩家列表
    List<String> getTopTenPlayers();

    // 设置冷却时间的结束时间
    void setCooldownData(long cooldownEnd);

    // 获取冷却时间的结束时间
    long getCooldownData();

    // 获取指定玩家的最新记录时间
    String getLatestRecordTime(String playerName);

    // 获取数据库中记录的总行数
    int getServerRecord();

    // 检查指定玩家奖励是否存在于数据库中
    boolean playerRewardExists(String playerName);
    // 检查指定玩家顶贴数据是否存在
    boolean playerDataExists(String playerName);

    // 插入玩家奖励数据
    void insertRewardData(String playerName);

    // 设置玩家的奖励数据，使用索引进行对应的更改
    void setRewardData(String playerName, int label);

    // 获取指定玩家的奖励数据，返回为字符串
    String getRewardDataAsString(String playerName);

    // 获取指定玩家今天的记录数
    int getPlayerTodayRecords(String playerName);

    boolean deleteRecords(String playerName, int numberOfRecords);
}