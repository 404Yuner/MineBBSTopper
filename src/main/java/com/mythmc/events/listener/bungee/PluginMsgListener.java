package com.mythmc.events.listener.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mythmc.MineBBSTopper;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMsgListener implements PluginMessageListener { // 定义 PluginMsgListener 类实现 PluginMessageListener 接口
    private final MineBBSTopper plugin; // 插件实例的引用

    public PluginMsgListener(MineBBSTopper plugin) { // 构造函数，接受一个 MineBBSTopper 插件实例
        this.plugin = plugin; // 初始化插件实例
    }

    public void load() { // 加载方法，用于注册插件消息通道
        MineBBSTopper.BUNGEECORE = plugin.getServer().spigot().getConfig().getBoolean("settings.bungeecord", false); // 检查配置文件中的 bungeecord 设置
        if (MineBBSTopper.BUNGEECORE) { // 如果启用了 BungeeCord
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord"); // 注册 outgoing 插件消息通道
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", new PluginMsgListener(plugin)); // 注册 incoming 插件消息通道
            plugin.logger("§d通信 §8| §a检测到您为代理模式，已注册插件通讯通道"); // 日志记录，表示已注册插件消息通道
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, org.bukkit.entity.Player player, byte[] message) { // 实现 PluginMessageListener 接口的 onPluginMessageReceived 方法
        if (!channel.equals("BungeeCord")) { // 如果消息不是来自 BungeeCord 通道
            return; // 不处理
        }
        try {
            // 解析消息
            ByteArrayDataInput in = ByteStreams.newDataInput(message); // 创建 ByteArrayDataInput 实例以读取消息
            String subChannel = in.readUTF(); // 读取子通道标识
            if (subChannel.equals("MineBBSBroadcast")) { // 如果子通道是 MineBBSBroadcast
                String meg = in.readUTF(); // 读取消息内容
                // 处理接收到的消息，广播到本地服务器上
                CommandUtil.broadcast(meg); // 使用 CommandUtil 工具类广播消息
            }
            if (subChannel.equals("MineBBSUpdateCache")) {
                TargetManager.refreshGlobalInfo();
                Debugger.logger("接收到其它子服发送到的缓存更新消息，本服的顶贴缓存已更新");
            }
        } catch (Exception e) { // 捕获处理消息时可能发生的异常
            plugin.logger("处理插件消息时出现异常：" + e.getMessage()); // 记录异常信息到日志
            e.printStackTrace(); // 打印异常堆栈
        }
    }

    public static void sendAlertToBungeeCord(String message) { // 发送警报消息到 BungeeCord
        if (MineBBSTopper.BUNGEECORE) { // 如果启用了 BungeeCord
            sendMessageToBungeeCord("MineBBSBroadcast", message); // 发送警报消息
        } else {
            CommandUtil.broadcast(message); // 如果未启用 BungeeCord，直接广播消息
        }
    }

    public static void sendUpdateRequestToBungeeCord() { // 发送更新请求到 BungeeCord
        if (MineBBSTopper.BUNGEECORE) { // 如果启用了 BungeeCord
            sendMessageToBungeeCord("MineBBSUpdateCache", "TODO"); // 发送更新请求
        }
    }

    private static void sendMessageToBungeeCord(String subChannel, String message) { // 发送消息到 BungeeCord
        ByteArrayDataOutput out = ByteStreams.newDataOutput(); // 创建 ByteArrayDataOutput 实例以写入消息
        out.writeUTF("Forward"); // BungeeCord 插件消息协议中的标识
        out.writeUTF("ALL"); // 发送给所有服务器
        out.writeUTF(subChannel); // 消息的子通道标识

        ByteArrayDataOutput msg = ByteStreams.newDataOutput(); // 创建另一个 ByteArrayDataOutput 实例用于写入消息内容
        msg.writeUTF(message); // 写入消息内容
        out.write(msg.toByteArray()); // 将消息内容写入到输出流中

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null); // 获取在线玩家列表中的第一个玩家
        if (player != null) { // 如果玩家不为空
            player.sendPluginMessage(MineBBSTopper.INSTANCE, "BungeeCord", out.toByteArray()); // 发送插件消息到 BungeeCord
        }
    }
}