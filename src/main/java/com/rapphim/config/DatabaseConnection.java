package com.rapphim.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton class quản lý kết nối CSDL MySQL.
 * Sử dụng JDBC để kết nối.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private String url;
    private String username;
    private String password;

    private DatabaseConnection() {
        loadConfig();
    }

    /**
     * Singleton: lấy instance duy nhất
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Đọc cấu hình từ file database.properties
     */
    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/database.properties")) {
            if (input == null) {
                System.err.println("Không tìm thấy file database.properties!");
                // Dùng cấu hình mặc định
                this.url = "jdbc:mysql://localhost:3306/rapphim_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
                this.username = "root";
                this.password = "";
                return;
            }
            Properties prop = new Properties();
            prop.load(input);
            this.url = prop.getProperty("db.url");
            this.username = prop.getProperty("db.username");
            this.password = prop.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Lỗi đọc file config: " + e.getMessage());
        }
    }

    /**
     * Lấy Connection tới database
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("✅ Kết nối Database thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy MySQL Driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối Database: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Đóng kết nối
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Đã đóng kết nối Database.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đóng kết nối: " + e.getMessage());
        }
    }
}
