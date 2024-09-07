package com.mythmc.impl.cache.target;

import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.database.DatabaseManager;
import com.mythmc.tools.Debugger;
import org.bukkit.entity.Player;

public class PlayerInfo {
    private final Player player;
    private final String playerName;
    private Integer amount;
    private String lastTimeStr;
    private String[] rewardData;
    private Integer todayAmount;

    public PlayerInfo(Player player) {
        this.player = player;
        this.playerName = player.getName();
        loadInitialData();
    }

    private void loadInitialData() {
        // 初始化时从数据库加载数据
        this.amount = DatabaseManager.getDbManager().countPlayerRecords(playerName);
        this.lastTimeStr = DatabaseManager.getDbManager().getLatestRecordTime(playerName);
        this.rewardData = DatabaseManager.getDbManager().getRewardDataAsString(playerName).split("-");
        this.todayAmount = DatabaseManager.getDbManager().getPlayerTodayRecords(playerName);
    }

    public Player getPlayer() {
        return player;
    }

    public int getAmount() {
        return this.amount;
    }

    public void addAmount(int amount) {
        this.amount = amount + getAmount();
        // 更新缓存
        TargetManager.getPlayerInfo(player).amount = this.amount;
    }
    public void deleteAmount(int amount) {
        this.amount = getAmount() - amount;
        // 更新缓存
        TargetManager.getPlayerInfo(player).amount = this.amount;
    }
    public String getLastTimeStr() {
        return this.lastTimeStr;
    }

    public void setLastTimeStr(String lastTime) {
        this.lastTimeStr = lastTime;
        DatabaseManager.getDbManager().insertPlayerData(playerName, lastTime);
    }

    public String[] getRewardData() {
        return this.rewardData;
    }

    public void setRewardData(String signal, int index) {
        if (index < rewardData.length) {
            rewardData[index] = signal;
            // 更新数据库
            DatabaseManager.getDbManager().setRewardData(playerName, index);
        } else {
            Debugger.logger("§c更新玩家 " + playerName + " 的累计奖励出错！！！奖励编号为：" + index+1);
        }
    }

    public int getTodayAmount() {
        return this.todayAmount;
    }

    public void addTodayAmount(int amount) {
        this.todayAmount = amount + getTodayAmount();
    }
}