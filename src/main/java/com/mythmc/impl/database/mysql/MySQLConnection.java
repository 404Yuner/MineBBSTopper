package com.mythmc.impl.database.mysql;



import com.mythmc.file.statics.ConfigFile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {

    public static HikariDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
    public static HikariConfig getHikariConfig() {
        // 新建连接池配置
        HikariConfig config = new HikariConfig();

        // 连接字符串参数
        String jdbcUrl = "jdbc:mysql://"
                + ConfigFile.MySQLHost
                + ":"
                + ConfigFile.MySQLPort
                + "/"
                + ConfigFile.MySQLDatabase
                + "?useSSL="
                + ConfigFile.MySQLUseSSL
                + "&allowPublicKeyRetrieval=true" // 允许公钥检索以支持旧认证插件
                + "&serverTimezone=UTC"; // 设置时区，以避免与时间相关的问题

        // 设置连接池参数
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(ConfigFile.MySQLUser);
        config.setPassword(ConfigFile.MySQLPassword);
        config.setMaximumPoolSize(ConfigFile.MySQLMaximumPoolSize); // 连接池最大连接数
        config.setConnectionTimeout(ConfigFile.MySQLConnectionTimeout); // 连接超时
        config.setIdleTimeout(ConfigFile.MySQLIdleTimeout); // 空闲超时
        config.setMaxLifetime(ConfigFile.MySQLMaxLifetime); // 最大生命周期

        return config;
    }
}

