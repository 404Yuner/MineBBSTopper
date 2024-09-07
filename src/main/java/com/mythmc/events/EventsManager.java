package com.mythmc.events;

import com.mythmc.MineBBSTopper;
import com.mythmc.events.listener.GUIListener;
import com.mythmc.events.listener.PlayerListener;
import com.mythmc.commands.gui.MainGUI;
import com.mythmc.commands.gui.RewardGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventsManager {
    private final MineBBSTopper plugin;
    public EventsManager(MineBBSTopper plugin) {
        this.plugin = plugin;
    }

    public void load() {
        // 注册事件
        Bukkit.getPluginManager().registerEvents((Listener) new PlayerListener(), (Plugin) plugin);
        Bukkit.getPluginManager().registerEvents((Listener) new MainGUI(), (Plugin) plugin);
        Bukkit.getPluginManager().registerEvents((Listener) new RewardGUI(), (Plugin) plugin);
        Bukkit.getPluginManager().registerEvents((Listener) new GUIListener(), (Plugin) plugin);
        plugin.logger("§a注册 §8| §a成功注册事件监听器");
    }
}
