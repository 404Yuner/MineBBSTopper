package com.mythmc.commands.gui.external;

import java.util.List;

public class SlotData {
    private List<Integer> slots; // 命令列表
    private String key; // 标记

    // 构造方法
    public SlotData(List<Integer> slots, String key) {
        this.slots = slots;
        this.key = key;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }
    public String getKey() {
        return key;
    }

    public void setKey(int require) {
        this.key = key;
    }
}
