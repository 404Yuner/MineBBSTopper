package com.mythmc.commands.gui.external;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomInventory {
    public static final Map<Inventory, CustomInventory> inventoryMap = new HashMap<>();
    private final Inventory inventory;
    private final String type;
    private final String playerName;

    public CustomInventory(Inventory inventory, String type, String playerName) {
        this.inventory = inventory;
        this.type = type;
        this.playerName = playerName;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getType() {
        return type;
    }
    public String getPlayerName() {
        return playerName;
    }
    public static void removeInventoryByPlayerName(String playerName) {
        Iterator<Map.Entry<Inventory, CustomInventory>> iterator = CustomInventory.inventoryMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Inventory, CustomInventory> entry = iterator.next();
            CustomInventory customInventory = entry.getValue();

            if (customInventory.getPlayerName().equals(playerName)) {
                // 找到对应的 CustomInventory，移除对应的 Inventory
                iterator.remove();
            }
        }
    }
}
