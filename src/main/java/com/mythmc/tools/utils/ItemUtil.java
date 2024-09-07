package com.mythmc.tools.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.exceptions.ProfileChangeException;
import com.cryptomorin.xseries.profiles.exceptions.UnknownPlayerException;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Pattern;

public class ItemUtil {
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+/\\-]{100,}={0,3}$");

    public static ItemStack handleItemStack(Player player, String mat, int amount) {
        ItemStack itemStack;
        if (!mat.startsWith("HEAD:")) {
            // 尝试使用 XMaterial 来获取 Material
            XMaterial xMaterial = XMaterial.matchXMaterial(mat).orElse(null);

            if (xMaterial != null) {
                // 创建 ItemStack 时使用 XMaterial.getMaterial()
                itemStack = new ItemStack(xMaterial.parseMaterial(), amount);
            } else {
                // 如果 XMaterial 无法匹配，则使用默认材料（例如 BEDROCK）
                itemStack = new ItemStack(Material.BEDROCK);
            }
        } else {
            // 获取玩家头颅材质
            itemStack = getSkullItemStack(player, mat);
        }
        return itemStack;
    }

    private static ItemStack getSkullItemStack(Player player, String input) {
        // 创建一个玩家头颅的 ItemStack
        ItemStack skull;
        String[] part = input.split(":");
        try {
            if (PATTERN.matcher(part[1]).matches()) {
                // 加载BASE64编码格式的头颅，并缓存
                skull = XSkull.createItem().profile(new Profileable.StringProfileable(part[1], ProfileInputType.BASE64)).apply();
            } else {
                if (part[1].equals("%PLAYER%")) {
                    // 加载玩家自己的头颅，并缓存
                    skull = XSkull.createItem().profile(new Profileable.PlayerProfileable(player)).apply();
                } else {
                    // 加载提供的玩家名头颅，并缓存
                    skull = XSkull.createItem().profile(new Profileable.UsernameProfileable(part[1])).apply();
                }
            }
            return skull;
        } catch (UnknownPlayerException | ProfileChangeException e) {
            // 默认的头颅材质
            return XSkull.createItem().profile(new Profileable.StringProfileable("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzEwNTkxZTY5MDllNmEyODFiMzcxODM2ZTQ2MmQ2N2EyYzc4ZmEwOTUyZTkxMGYzMmI0MWEyNmM0OGMxNzU3YyJ9fX0=", ProfileInputType.BASE64)).apply();
        }
    }

    public static ItemMeta addGlow(ItemMeta itemMeta) {
        if (itemMeta != null) {
            // 添加伪附魔效果（光效），但不会显示实际的附魔标记
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            // 隐藏附魔标记
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return itemMeta;
    }

    public static ItemMeta addItemFlags(ItemMeta meta, List<String> flagStrings) {
        if (meta == null || flagStrings == null) {
            return meta; // 如果输入无效，返回原始的 meta 对象
        }

        for (String flagString : flagStrings) {
            if (flagString != null && !flagString.isEmpty()) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(flagString.toUpperCase());
                    meta.addItemFlags(flag);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return meta;
    }
}
