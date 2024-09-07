package com.mythmc.commands.gui;

import com.mythmc.MineBBSTopper;
import com.mythmc.commands.gui.external.SlotData;
import com.mythmc.commands.gui.holder.GUIType;
import com.mythmc.commands.gui.holder.MineBBSTopperGUIHolder;
import com.mythmc.externs.hook.placeholders.PlaceholderCache;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.impl.cache.TargetManager;
import com.mythmc.impl.cache.target.GlobalInfo;
import com.mythmc.impl.cache.target.PlayerInfo;
import com.mythmc.tools.utils.ColorUtil;
import com.mythmc.tools.utils.ItemUtil;
import com.mythmc.tools.utils.PAPIUtil;
import com.mythmc.tools.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MainGUI implements Listener {
    // 定义一个静态的Map，用于存储槽位列表和对应的命令列表
    public static Map<SlotData, List<String>> slotCommands;
    public static Map<String, Inventory> cachedMainGUI = new HashMap<>();

    // MainGUI类的构造函数，初始化槽位命令
    public MainGUI() {
        initializeSlotCommands();
    }

    // 初始化槽位命令的方法
    public void initializeSlotCommands() {
        slotCommands = null; // 先将slotCommands设为null
        slotCommands = new HashMap<>(); // 初始化slotCommands为一个空的HashMap
        ConfigurationSection menuItems = GUIFile.mainMenuItems; // 从GUIFile中获取菜单项配置
        List<String> layout = GUIFile.mainMenuLayout; // 获取槽位配置
        // 遍历所有菜单项的键
        for (String key : menuItems.getKeys(false)) {
            // 新建槽位列
            List<Integer> slots = new ArrayList<>();

            ConfigurationSection itemSection = menuItems.getConfigurationSection(key); // 获取每个菜单项的配置

            List<String> commands = itemSection.getStringList("commands"); // 获取命令列表

            if (layout != null) {

                int lineWidth = 9; // 固定行宽
                int previousEndIndex = -1;

                // 遍历每一行
                for (String line : layout) {
                    char[] chars = line.toCharArray();
                    int effectiveLineLength = Math.min(chars.length, lineWidth); // 实际使用的行长度（不超过lineWidth）

                    // 遍历每一行中的每个字符
                    for (int index = 0; index < effectiveLineLength; index++) {
                        // 如果索引超出实际字符长度，则视为非key字符
                        if (index < chars.length && String.valueOf(chars[index]).equals(key)) {
                            // 计算索引值，累积上一个“完整”行的结束索引（即上一个行的lineWidth）
                            int adjustedIndex = index + (previousEndIndex + 1);
                            slots.add(adjustedIndex);
                        }
                        // 如果索引达到lineWidth但行字符未结束，则不添加任何内容
                    }
                    // 更新上一“完整”行的结束索引
                    previousEndIndex += lineWidth;
                }

            } else {
                Object slotObj = itemSection.get("slot"); // 获取槽位配置

                // 如果槽位配置是一个整数
                if (slotObj instanceof Integer) {
                    int slot = (Integer) slotObj; // 将槽位配置转为整数
                    // 将整数槽位转为单元素列表
                    slots = Collections.singletonList(slot);
                }
                // 如果槽位配置是一个整数列表
                else if (slotObj instanceof List) {
                    slots = (List<Integer>) slotObj; // 将槽位配置转为整数列表
                }
            }
            slotCommands.put(new SlotData(slots,key), commands); // 将槽位列表和命令列表存入slotCommands
        }
    }

    // 打开菜单的方法
    public static void openMenu(Player player) {

        String playerName = player.getName();
        // 查找缓存，判断是否需要重新生成
        if (cachedMainGUI.containsKey(playerName)) {
            player.openInventory(cachedMainGUI.get(playerName));
        } else {
            // 创建一个新的Inventory对象，标题根据是否使用PlaceholderHook来决定
            Inventory mainGUI = Bukkit.createInventory(new MineBBSTopperGUIHolder(GUIType.MAIN), GUIFile.mainMenuSize, PAPIUtil.set(player, GUIFile.mainMenuName));

            // 从GUIFile中获取菜单项配置
            ConfigurationSection menuItems = GUIFile.mainMenuItems;

            // 获取玩家信息
            PlayerInfo playerInfo = TargetManager.getPlayerInfo(player);
            int amount = playerInfo.getAmount(); // 获取玩家记录的数量
            String lastTime = playerInfo.getLastTimeStr(); // 获取玩家的最新记录时间

            // 获取全局信息
            GlobalInfo globalInfo = TargetManager.getGlobalInfo();
            long cooldownTimestamp = globalInfo.getCooldownTimestamp(); // 获取冷却时间戳
            String cooldownTimeStr = globalInfo.getCooldownTimeStr(); // 获取冷却时间数据

            // 判断是否可以顶贴
            String cantop = cooldownTimestamp <= System.currentTimeMillis() / 1000 ? "§a可顶贴" : "§c冷却中";
            cantop = TimeUtil.isCurrentHourWithinSpan() ? cantop : "§c不在可顶贴时间段"; // 判断是否在顶贴时间段内

            Set<SlotData> slotsSet = slotCommands.keySet();
            List<SlotData> slots = new ArrayList<>(slotsSet);
            // 遍历所有菜单项
            for (String key : menuItems.getKeys(false)) {
                ConfigurationSection itemSection = menuItems.getConfigurationSection(key); // 获取每个菜单项的配置

                ItemStack itemStack; // 定义ItemStack对象
                String materialName = itemSection.getString("mat", "BEDROCK").toUpperCase(); // 获取物品材质名称，默认为BEDROCK

                itemStack = ItemUtil.handleItemStack(player, materialName, itemSection.getInt("amount", 1)); // 处理物品堆栈
                ItemMeta itemMeta = itemStack.getItemMeta(); // 获取物品的Meta数据

                // 设置物品的显示名称，考虑是否使用PlaceholderHook
                itemMeta.setDisplayName(ColorUtil.colorize(PAPIUtil.set(player, itemSection.getString("name", " "))));
                // 如果不是低版本，则设置自定义模型数据
                if (!MineBBSTopper.isLowVersion) itemMeta.setCustomModelData(itemSection.getInt("cmd", 0));

                // 处理发光逻辑
                if (itemSection.getBoolean("glow", false)) itemMeta = ItemUtil.addGlow(itemMeta);

                // 处理flag
                itemMeta = ItemUtil.addItemFlags(itemMeta, (List<String>) itemSection.getList("flag"));

                // 处理lore
                List<String> lore = itemSection.getStringList("lore"); // 获取物品的lore
                List<String> cachedRank = PlaceholderCache.getCache(); // 获取缓存的排名数据


                int rankInsertIndex = -1; // 用于记录%rank%所在的索引
                int size = lore.size();
                // 遍历lore，找到%rank%并记录索引，同时处理%amount%
                for (int i = 0; i < size; i++) {
                    String line = lore.get(i);
                    if (line.contains("%rank%")) {
                        lore.remove(i);
                        rankInsertIndex = i; // %rank%的下一行开始插入
                        break; // 找到第一个%rank%后就退出循环
                    }

                    String result = line
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%lasttime%", lastTime)
                            .replace("%cantop%", cantop)
                            .replace("%cooldown%", cooldownTimeStr);

                    lore.set(i, result); // 更新lore
                }

                // 如果找到了%rank%，并且排名列表不为空
                if (rankInsertIndex != -1 && !cachedRank.isEmpty()) {
                    // 从rankInsertIndex开始，将所有排名添加到lore中
                    for (String rank : cachedRank) {
                        lore.add(rankInsertIndex++, rank);
                    }
                }
                // 处理papi
                lore = ColorUtil.colorize(PAPIUtil.set(player, lore));
                itemMeta.setLore(lore); // 设置物品的lore
                itemStack.setItemMeta(itemMeta); // 将Meta数据应用到物品

                // 处理槽位
                slots.stream()
                        .filter(temp -> temp.getKey().equals(key)) // 筛选符合条件的 SlotData
                        .flatMap(temp -> temp.getSlots().stream()) // 展平所有符合条件的槽位
                        .forEach(slot -> mainGUI.setItem(slot, itemStack)); // 将物品设置到每个槽位

                player.openInventory(mainGUI); // 打开菜单给玩家
                cachedMainGUI.put(playerName,mainGUI); // 放入缓存
            }
        }
    }

}