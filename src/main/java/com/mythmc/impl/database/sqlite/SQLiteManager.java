package com.mythmc.impl.database.sqlite;

import com.mythmc.api.DbManager;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.impl.database.DatabaseManager;
import com.mythmc.tools.Debugger;
import com.mythmc.tools.utils.TimeUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SQLiteManager implements DbManager {
    public static Connection connection;

    /**
     * 注释请参考 MySQLManager 三个都一样的
     */
    public void createTable() {
        try {
            String url = "jdbc:sqlite:" + DatabaseManager.dbPath;
            connection = DriverManager.getConnection(url);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        String playerDataTable = "CREATE TABLE IF NOT EXISTS player_data (" +
                "player TEXT," +
                "recordTime TEXT" +
                ")";
        String cooldownDataTable = "CREATE TABLE IF NOT EXISTS cooldown_data (" +
                "cooldown LONG" +
                ")";
        String rewardDataTable = "CREATE TABLE IF NOT EXISTS reward_data (" +
                "player TEXT," +
                "reward1 INTEGER," +
                "reward2 INTEGER," +
                "reward3 INTEGER," +
                "reward4 INTEGER," +
                "reward5 INTEGER," +
                "reward6 INTEGER," +
                "reward7 INTEGER," +
                "reward8 INTEGER," +
                "reward9 INTEGER," +
                "reward10 INTEGER" +
                ")";
        executeUpdate(playerDataTable);
        executeUpdate(cooldownDataTable);
        executeUpdate(rewardDataTable);
    }

    private void executeUpdate(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayerData(String playerName, String time) {
        String sql = "INSERT INTO player_data (player, recordTime) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.setString(2, time);
            statement.executeUpdate();
            Debugger.logger("已记录玩家 " + playerName + " 顶贴记录");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countPlayerRecords(String playerName) {
        String sql = "SELECT COUNT(*) AS count FROM player_data WHERE player = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getInt("count");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getTopTenPlayers() {
        String sql = "SELECT player, COUNT(*) AS count FROM player_data GROUP BY player ORDER BY count DESC LIMIT ?";

        List<String> topTenPlayers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ConfigFile.rankPlayer);
            ResultSet rs = pstmt.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (topTenPlayers.isEmpty()) {
            topTenPlayers.add("§7虚位以待");
        }
        return topTenPlayers;
    }
    public long getCooldownData() {
        String sql = "SELECT cooldown FROM cooldown_data";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long value = rs.getLong("cooldown");
                return value;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setCooldownData(long newCooldownValue) {
        String sql;
        if (getCooldownData() != 0) {
            sql = "UPDATE cooldown_data SET cooldown = ?";
        } else {
            sql = "INSERT INTO cooldown_data (cooldown) VALUES (?)";
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, newCooldownValue);
            pstmt.executeUpdate();
            Debugger.logger("成功设置数据库中下一次可顶贴的时间为 " + TimeUtil.convertTimestamp(newCooldownValue));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLatestRecordTime(String playerName) {
        String sql = "SELECT recordTime FROM player_data WHERE player = ? ORDER BY recordTime DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("recordTime");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "暂无顶贴";
    }

    public int getServerRecord() {
        String sql = "SELECT COUNT(*) FROM player_data";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /// 奖励
    public boolean playerRewardExists(String playerName) {
        String sql = "SELECT * FROM reward_data WHERE player = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean playerDataExists(String playerName) {
        String sql = "SELECT * FROM player_data WHERE player = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void insertRewardData(String playerName) {
        String sql = "INSERT INTO reward_data (player, reward1, reward2, reward3, reward4, reward5, reward6, reward7, reward8, reward9, reward10) VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRewardData(String playerName, int label) {

        String sql = "UPDATE reward_data SET reward" + label + " = 1 WHERE player = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.executeUpdate();
            Debugger.logger("已将玩家 " + playerName + " 的 " + label + " 号累计奖励领取数据写入数据库");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getRewardDataAsString(String playerName) {
        StringBuilder rewardsBuilder = new StringBuilder();
        String sql = "SELECT reward1, reward2, reward3, reward4, reward5, reward6, reward7, reward8, reward9, reward10 FROM reward_data WHERE player = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
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
            e.printStackTrace();
        }

        return rewardsBuilder.toString(); // 返回用 - 分隔的字符串
    }

    public int getPlayerTodayRecords(String playerName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = LocalDate.now().format(formatter);

        String sql = "SELECT COUNT(*) as appearance_count " +
                "FROM player_data " +
                "WHERE DATE(recordTime) = ? AND player = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, todayDate);
            statement.setString(2, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.getInt("appearance_count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}