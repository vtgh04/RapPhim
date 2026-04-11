package com.rapphim.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton quản lý kết nối JDBC tới Microsoft SQL Server.
 *
 * <p>
 * Driver yêu cầu: {@code com.microsoft.sqlserver:mssql-jdbc} — đã khai báo
 * trong pom.xml.
 * </p>
 *
 * <p>
 * Cấu hình kết nối chỉnh ở các hằng số bên dưới.
 * </p>
 */
public class DatabaseConnection {

    // ── Cấu hình kết nối SQL Server ─────────────────────────────────────────
    // SQL Server Authentication: sa / 123
    private static final String SERVER   = "localhost\\SQLEXPRESS";
    private static final String PORT     = "1433";
    private static final String DATABASE = "RapPhim";

    private static final String URL =
            "jdbc:sqlserver://" + SERVER + ":" + PORT + ";"
            + "databaseName="          + DATABASE + ";"
            + "encrypt=false;"
            + "trustServerCertificate=true;"
            + "loginTimeout=5;"
            + "connectRetryCount=0;";

    private static final String USER     = "sa";
    private static final String PASSWORD = "123";


    // ── Singleton instance ───────────────────────────────────────────────────
    private static Connection instance;

    private DatabaseConnection() {
    }

    /**
     * Trả về Connection duy nhất; tạo mới nếu chưa có hoặc đã đóng.
     *
     * @return {@link Connection} đang mở tới SQL Server
     * @throws SQLException nếu không kết nối được
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                        "SQL Server JDBC Driver không tìm thấy. " +
                                "Hãy thêm 'mssql-jdbc' vào pom.xml.",
                        e);
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
            } catch (SQLException ignored) {
            }
            instance = null;
        }
    }
}
