# MineBBSTopper
![](https://img.shields.io/github/stars/404Yuner/MineBBSTopper) ![](https://img.shields.io/github/issues/404Yuner/MineBBSTopper?label=Issues) ![](https://img.shields.io/github/license/404Yuner/MineBBSTopper)

MineBBSTopper 是一个 MineBBS 论坛顶贴检测插件，可以判断顶贴并在服务器内发放奖励

# 功能

- 支持 MySQL / SQLite / Yaml 三种数据存储方式
- 支持 Folia 核心
- 全异步操作，不卡主线程！
- 支持 ItemsAdder oraxen
- 提供多种动作指令
- 缓存机制大幅优化性能
- 可选玩家顶贴时间
- 全局奖励冷却时间
- 支持发放累计顶贴奖励
- 支持自定义头颅材质
- 支持特定日期额外奖励
- 支持全息图排行榜
- 支持彩色文本
- 支持自定义菜单界面
- 版本更新快，问题少​

# 使用须知​

获取顶贴的原理是获取宣传贴上顶贴时间的时间戳，而非调用MINEBBS的接口。

由于极少数服务器的宣传贴格式无顶贴时间戳，请查看插件配置说明进行更改。

请在启动插件时，查看后台记录获取到的顶贴时间是否和宣传贴网站上的顶贴时间相同。

**插件没有WIKI，文件内自带使用注释​**

# 链接

- [GitHub](https://github.com/404Yuner/MineBBSTopper)
- [MineBBS](https://www.minebbs.com/resources/.8762/)
- QQ群：[301662621](https://qm.qq.com/q/Ng6wI3Ctaw​)

# 测试情况​

| 核心             | 可用版本       | 备注                         |
| ---------------- | -------------- | ---------------------------- |
| Spigot​           | 1.7-1.21​       | 1.12以下用Yaml存储数据​       |
| Paper            |​ 1.7-1.21​       | 同上​                         |
| Purpur​           | 1.7-1.21​       | 同上​                         |
| Leaf/Leaves​      | 1.7-1.21​       | 同上​                         |
| CatSever (Forge)​ | 1.12 1.16 1.18​ | 1.12用不了头颅材质，其它正常​ |
| Arclight (Forge)​ | 无法使用​       | 无                           |
| Mohist (Forge)​   | 1.20​           | 无                           |
| 其它端​           | 未测试         | 不接受兼容需求​               |

# Command

| 命令                                | 介绍                     | 权限节点             |
| ----------------------------------- | ------------------------ | -------------------- |
| /minebbstopper open [main/reward]   | 打开顶贴菜单             | 无                   |
| /minebbstopper reload               | 重载插件                 | minebbstopper.reload |
| /minebbstopper test [normal/offday] | 测试奖励指令             | minebbstopper.test   |
| /minebbstopper url                  | 测试获取宣传贴的顶贴时间 | minebbstopper.test   |

# action
**具体使用方法请查看插件配置文件**

- [console]
- [player]
- [op]
- [tell]
- [broadcast]
- [delay]
- [give-money]
- [give-points]
- [title]
- [bossbar]
- [connect]
- [close]
- [open]
- [sound]​

# PlaceHolderAPI

**【全局类型变量】**
- `%minebbstopper_global_cantop%` 返回是否可顶贴的布尔值(不含时间段情况)
- `%minebbstopper_global_count%` 返回服务器总的顶贴次数
- `%minebbstopper_global_cooldown_<格式类型:iso或long>%` 返回其中一种全局冷却时间的格式
- `%minebbstopper_global_rank_<任意整数位次>%` 返回顶贴排行榜上指定位次的排名情况

**【玩家类型变量】**
- `%minebbstopper_player_count%` 返回玩家总的顶贴次数
- `%minebbstopper_player_todaycount%` 返回玩家当日的顶贴次数
- `%minebbstopper_player_lasttime%` 返回玩家上次顶贴的时间
- `%minebbstopper_player_countother_<玩家名>%` 返回指定玩家总的顶贴次数
- `%minebbstopper_player_reward_<奖励id>%` 返回玩家此奖励状态的布尔值​

# Bstats

[![](https://bstats.org/signatures/bukkit/MineBBSTopper.svg)](https://bstats.org/plugin/bukkit/MineBBSTopper/22565​)
