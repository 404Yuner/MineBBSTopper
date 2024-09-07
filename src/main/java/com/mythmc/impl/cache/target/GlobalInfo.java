package com.mythmc.impl.cache.target;

import com.mythmc.impl.database.DatabaseManager;
import com.mythmc.tools.utils.TimeUtil;

public class GlobalInfo {

    private Integer amount;
    private Long cooldownTimestamp;
    private String cooldownTimeStr;
    private static final long THRESHOLD = 10000000000L;

    // 私有构造函数，防止外部直接实例化
    public GlobalInfo() {
        loadInitialData();
    }


    public void loadInitialData() {
        // 在创建实例时初始化缓存数据
        amount = DatabaseManager.getDbManager().getServerRecord();
        long temp = DatabaseManager.getDbManager().getCooldownData();
        boolean isMillisecond = temp > THRESHOLD;
        cooldownTimestamp = isMillisecond ? temp / 1000L : temp;
        cooldownTimeStr = TimeUtil.convertTimestamp(cooldownTimestamp);
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount(int amount) {
        this.amount = amount + getAmount();
    }
    public void deleteAmount(int amount) {
        this.amount = getAmount() - amount ;
    }
    public long getCooldownTimestamp() {
        return cooldownTimestamp;
    }

    public void setCooldownTimestamp(long cooldownTimestamp) {
        DatabaseManager.getDbManager().setCooldownData(cooldownTimestamp);
        this.cooldownTimestamp = cooldownTimestamp;
       // this.cooldownTimeStr = TimeUtil.convertTimestamp(cooldownTimestamp);
    }

    public String getCooldownTimeStr() {
        return cooldownTimeStr;
    }

    public void setCooldownTimeStr(String cooldownTimeData) {
        this.cooldownTimeStr = cooldownTimeData;
    }
}