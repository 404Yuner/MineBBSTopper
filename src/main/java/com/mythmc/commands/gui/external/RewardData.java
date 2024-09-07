package com.mythmc.commands.gui.external;

import java.util.List;

public class RewardData {
    private List<String> commands; // 命令列表
    private int require; // 需要的次数
    private int rewardLabel; // 奖励标签

    // 构造方法
    public RewardData(List<String> commands, int require, int rewardData) {
        this.commands = commands;
        this.require = require;
        this.rewardLabel = rewardData;
    }

    // 获取命令列表
    public List<String> getCommands() {
        return commands;
    }

    // 设置命令列表
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    // 获取需要的次数
    public int getRequire() {
        return require;
    }

    // 设置需要的次数
    public void setRequire(int require) {
        this.require = require;
    }

    // 获取奖励标签
    public int getRewardLabel() {
        return rewardLabel;
    }

    // 设置奖励标签
    public void setRewardLabel(int rewardData) {
        this.rewardLabel = rewardData;
    }
}
