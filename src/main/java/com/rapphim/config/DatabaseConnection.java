package com.rapphim.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton quản lý kết nối JDBC tới MySQL.
 * <p>
 * Cấu hình qua các hằng số bên dưới (hoặc đọc từ file properties nếu cần).
 * </p>
 */
public class DatabaseConnection {

    // ── Cấu hình kết nối ────────────────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/rapphim"
                                         + "?useUnicode=true"
                                         + "&characterEncoding=UTF-8"
                                         + "&serverTimezone=Asia/Ho_Chi_Minh"
                                         + "&useSSL=false"
                                         + "&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "";          // Đổi thành mật khẩu MySQL của bạn

    // ── Singleton instance ───────────────────────────────────────────────────
    private static Connection instance;

    private DatabaseConnection() {}

    /**
     * Trả về Connection duy nhất; tạo mới nếu chưa có hoặc đã đóng.
     *
     * @return {@link Connection} đang mở tới DB
     * @throws SQLException nếu không kết nối được
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }

    /**
     * Đóng kết nối hiện tại (gọi khi ứng dụng tắt).
     */
    public static void close() {
        if (instance != null) {
            try {
                instance.close();
            } catch (SQLException ignored) {}
            instance = null;
        }
    }
}
