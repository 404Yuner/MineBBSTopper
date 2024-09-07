package com.mythmc.commands.gui.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MineBBSTopperGUIHolder implements InventoryHolder {
    private final GUIType guiType;

    public MineBBSTopperGUIHolder(GUIType guiType) {
        this.guiType = guiType;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public GUIType getGuiType() {
        return guiType;
    }
}
