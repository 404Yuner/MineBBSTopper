package com.mythmc.impl.database.mysql;



import com.mythmc.api.DbManager;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.TimeUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MySQLManager implements DbManager {

    // 创建表的函数
    public void createTable() {
        // 创建 minebbstopper_data 表，包含玩家名称和记录时间
        String sql1 = "CREATE TABLE IF NOT EXISTS minebbstopper_data (" +
                "player TEXT," +
                "recordTime TEXT" +
                ")";
        // 创建 minebbstopper_cooldown 表，包含冷却时间
        String sql2 = "CREATE TABLE IF NOT EXISTS minebbstopper_cooldown (" +
                "cooldown BIGINT" +
                ")";
        // 创建 minebbstopper_reward 表，包含玩家及其奖励信息
        String rewardDataTable = "CREATE TABLE IF NOT EXISTS minebbstopper_reward (" +
                "player TEXT," +
                "reward1 INT," +
                "reward2 INT," +
                "reward3 INT," +
                "reward4 INT," +
                "reward5 INT," +
                "reward6 INT," +
                "reward7 INT," +
                "reward8 INT," +
                "reward9 INT," +
                "reward10 INT" +
                ")";
        //    MineBBSTopper.getPlugin().getLogger().info("正在创建mysql表");
        try (Connection connection = MySQLConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // 执行 SQL 语句创建表
            statement.executeUpdate(sql1);
            statement.executeUpdate(sql2);
            statement.executeUpdate(rewardDataTable);
            //MineBBSTopper.getPlugin().getLogger().info("成功执行sql");
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
    }
    /**
     * 插入玩家顶贴数据的函数
     *
     * @param playerName 玩家名字
     * @param time 顶贴时间
     */
    public void insertPlayerData(String playerName, String time) {
        // 插入数据的 SQL 语句
        String sql = "INSERT INTO minebbstopper_data (player, recordTime) VALUES (?, ?)";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // 设置参数值
            statement.setString(1, playerName);
            statement.setString(2, time);
            // 执行插入操作
            statement.executeUpdate();
            Debugger.logger("已记录玩家 " + playerName + " 顶贴记录"); // 记录调试信息
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
    }
    /**
     * 统计玩家记录数量的函数
     *
     * @param playerName 玩家名字
     * @return 返回玩家总的顶贴次数
     */
    public int countPlayerRecords(String playerName) {
        // 查询玩家记录数量的 SQL 语句
        String sql = "SELECT COUNT(*) AS count FROM minebbstopper_data WHERE player = ?";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // 设置查询参数
            statement.setString(1, playerName);
            try (ResultSet rs = statement.executeQuery()) {
                // 获取并返回记录数量
                if (rs.next()) return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
        return 0; // 默认返回 0
    }
    /**
     * 获取前十名玩家的函数
     *
     * @return 前十名玩家格式化后的列表
     */
    public List<String> getTopTenPlayers() {
        // 查询前十名玩家的 SQL 语句
        String sql = "SELECT player, COUNT(*) AS count FROM minebbstopper_data GROUP BY player ORDER BY count DESC LIMIT ?";
        List<String> topTenPlayers = new ArrayList<>(); // 存储前十名玩家的列表
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, ConfigFile.rankPlayer); // 设置参数
            try (ResultSet rs = pstmt.executeQuery()) {
                int rank = 1; // 初始化排名为 1
                while (rs.next()) {
                    String playerName = rs.getString("player");
                    int count = rs.getInt("count");
                    // 格式化并添加排名信息
                    String formattedInfo = GUIFile.rankFormat
                            .replace("%player%", playerName)
                            .replace("%count%", String.valueOf(count))
                            .replace("%rank%", String.valueOf(rank));
                    topTenPlayers.add(formattedInfo);
                    rank++; // 更新排名
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 如果列表为空，添加占位信息
        if (topTenPlayers.isEmpty()) {
            topTenPlayers.add("§7虚位以待");
        }
        return topTenPlayers; // 返回前十名玩家列表
    }

    /**
     * 查询冷却时间的函数
     *
     * @return 返回冷却结束时间
     */
    public long getCooldownData() {
        // 查询冷却时间的 SQL 语句
        String sql = "SELECT cooldown FROM minebbstopper_cooldown";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // 获取并返回冷却时间
            if (rs.next()) {
                long value = rs.getLong("cooldown");
                return value;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
        return 0; // 默认返回 0
    }
    /**
     * 设置冷却时间的函数
     *
     * @param newCooldownValue 冷却结束时间
     */
    public void setCooldownData(long newCooldownValue) {
        // 根据是否存在冷却时间记录选择 SQL 语句
        String sql = (getCooldownData() != 0) ? "UPDATE minebbstopper_cooldown SET cooldown = ?" : "INSERT INTO minebbstopper_cooldown (cooldown) VALUES (?)";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 设置冷却时间值
            pstmt.setLong(1, newCooldownValue);
            int affectedRows = pstmt.executeUpdate(); // 执行插入或更新操作
            if (affectedRows == 0) throw new SQLException("插入或更新数据失败，没有冷却受到影响。"); // 如果没有数据被影响，抛出异常
            Debugger.logger("成功设置数据库中下一次可顶贴的时间为 " + TimeUtil.convertTimestamp(newCooldownValue)); // 记录调试信息
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
    }
    /**
     * 获取玩家最新记录时间的函数。
     *
     * @param playerName 玩家名称
     * @return 玩家最近一次的顶贴记录
     */
    public String getLatestRecordTime(String playerName) {
        // 查询玩家最新记录时间的 SQL 语句
        String sql = "SELECT recordTime FROM minebbstopper_data WHERE player = ? ORDER BY UNIX_TIMESTAMP(recordTime) DESC LIMIT 1";
        String latestRecordTime = "暂无顶贴"; // 默认返回值
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 设置查询参数
            pstmt.setString(1, playerName);
            try (ResultSet rs = pstmt.executeQuery()) {
                // 获取并返回最新记录时间
                if (rs.next()) latestRecordTime = rs.getString("recordTime");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
        return latestRecordTime; // 返回最新记录时间
    }

    /**
     * 获取全局的顶贴记录数量。
     *
     * @return 服务器顶贴的数量
     */
    public int getServerRecord() {
        // 查询记录总行数的 SQL 语句
        String sql = "SELECT COUNT(*) FROM minebbstopper_data";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                // 获取并返回总行数
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 捕获并打印 SQL 异常
        }
        return 0; // 默认返回 0
    }


    /**
     * 检查玩家是否存在于奖励数据库中。
     *
     * @param playerName 玩家名称
     * @return 如果玩家存在则返回 true，反之返回 false
     */
    public boolean playerRewardExists(String playerName) {
        String sql = "SELECT * FROM minebbstopper_reward WHERE player = ?";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // 设置 SQL 查询参数
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            // 如果有结果集，则玩家存在
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // 出现异常或无结果返回 false
    }
    /**
     * 检查玩家是否存在于顶贴记录数据库中。
     *
     * @param playerName 玩家名称
     * @return 如果玩家存在则返回 true，反之返回 false
     */
    public boolean playerDataExists(String playerName) {
        String sql = "SELECT * FROM minebbstopper_data WHERE player = ?";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // 设置 SQL 查询参数
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            // 如果有结果集，则玩家存在
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // 出现异常或无结果返回 false
    }
    /**
     * 向数据库插入新的奖励数据。
     * 如果玩家尚不存在，则为他们插入初始的奖励记录（所有奖励值为 0）。
     *
     * @param playerName 玩家名称
     */
    public void insertRewardData(String playerName) {
        String sql = "INSERT INTO minebbstopper_reward (player, reward1, reward2, reward3, reward4, reward5, reward6, reward7, reward8, reward9, reward10) VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 设置 SQL 插入参数
            pstmt.setString(1, playerName);
            pstmt.executeUpdate(); // 执行插入操作
        } catch (SQLException e) {
            e.printStackTrace(); // 打印异常栈
        }
    }

    /**
     * 更新指定玩家的某一奖励领取状态。
     *
     * @param playerName 玩家名称
     * @param label 奖励标签（1-10）
     */
    public void setRewardData(String playerName, int label) {

        String sql = "UPDATE minebbstopper_reward SET reward" + label + " = 1 WHERE player = ?";

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // 设置 SQL 更新参数
            pstmt.setString(1, playerName);
            pstmt.executeUpdate(); // 执行更新操作
            Debugger.logger("已将玩家 " + playerName + " 的 " + label + " 号累计奖励领取数据写入数据库");
        } catch (SQLException e) {
            e.printStackTrace(); // 打印异常栈
        }
    }

    /**
     * 获取指定玩家的奖励数据并以字符串形式返回（奖励值用 '-' 分隔）。
     *
     * @param playerName 玩家名称
     * @return 奖励数据字符串（如 "0-1-0-..."）
     */
    public String getRewardDataAsString(String playerName) {
        StringBuilder rewardsBuilder = new StringBuilder();
        String sql = "SELECT reward1, reward2, reward3, reward4, reward5, reward6, reward7, reward8, reward9, reward10 FROM minebbstopper_reward WHERE player = ?";

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 设置 SQL 查询参数
            pstmt.setString(1, playerName);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {  // 确保有结果
                    for (int i = 1; i <= 10; i++) {
                        int rewardValue = resultSet.getInt("reward" + i);
                        // 将整数添加到 StringBuilder
                        rewardsBuilder.append(rewardValue);
                        // 在不是最后一个奖励的时候添加分隔符
                        if (i < 10) {
                            rewardsBuilder.append("-");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 打印异常栈
        }

        return rewardsBuilder.toString(); // 返回用 '-' 分隔的字符串
    }

    /**
     * 获取指定玩家今天的记录数量。
     *
     * @param playerName 玩家名称
     * @return 玩家今天的记录数量
     */
    public int getPlayerTodayRecords(String playerName) {
        // 格式化当前日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = LocalDate.now().format(formatter);

        // SQL 查询语句
        String sql = "SELECT COUNT(*) as appearance_count " +
                "FROM minebbstopper_data " +
                "WHERE DATE(recordTime) = ? AND player = ?";

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 设置日期参数
            pstmt.setString(1, todayDate);
            // 设置玩家名字参数
            pstmt.setString(2, playerName);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // 如果有记录，返回计数
                if (resultSet.next()) {
                    return resultSet.getInt("appearance_count");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // 打印异常栈
        }
        // 如果没有记录或出现异常，返回 0
        return 0;
    }
}
