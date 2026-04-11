package com.rapphim.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Class test kết nối SQL Server — chạy độc lập để debug.
 * Chạy: chuột phải → Run As → Java Application
 */
public class TestConnection {

    public static void main(String[] args) {
        System.out.println("=== Test SQL Server Connection ===");

        // ── Thử Windows Authentication ──────────────────────────────────
        testConnection(
            "jdbc:sqlserver://localhost:1433;"
            + "databaseName=master;"          // dùng master để test, không cần RapPhim có sẵn
            + "integratedSecurity=true;"
            + "encrypt=false;"
            + "trustServerCertificate=true;"
            + "loginTimeout=5;",
            "", "",
            "Windows Authentication (integratedSecurity=true)"
        );

        // ── Thử SQL Server Authentication với sa ───────────────────────
        // Đổi PASSWORD bên dưới nếu bạn biết mật khẩu sa
        testConnection(
            "jdbc:sqlserver://localhost:1433;"
            + "databaseName=master;"
            + "encrypt=false;"
            + "trustServerCertificate=true;"
            + "loginTimeout=5;",
            "sa", "",    // ← thay "" bằng mật khẩu sa nếu có
            "SQL Auth - sa / (no password)"
        );

        // ── Thử tên instance SQLEXPRESS ────────────────────────────────
        testConnection(
            "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
            + "databaseName=master;"
            + "integratedSecurity=true;"
            + "encrypt=false;"
            + "trustServerCertificate=true;"
            + "loginTimeout=5;",
            "", "",
            "Windows Auth + instance SQLEXPRESS"
        );
    }

    private static void testConnection(String url, String user, String pass, String label) {
        System.out.println("\n--- " + label + " ---");
        System.out.println("URL: " + url);
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = user.isEmpty()
                    ? DriverManager.getConnection(url)
                    : DriverManager.getConnection(url, user, pass);

            System.out.println("✅ KẾT NỐI THÀNH CÔNG!");

            // In tên server và version SQL Server
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT @@SERVERNAME, @@VERSION")) {
                if (rs.next()) {
                    System.out.println("   Server name : " + rs.getString(1));
                    System.out.println("   Version     : " + rs.getString(2).split("\n")[0]);
                }
            }
            conn.close();

        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver không tìm thấy: " + e.getMessage());
            System.out.println("   → Kiểm tra sqljdbc42.jar trong lib/jdbc/");
        } catch (Exception e) {
            System.out.println("❌ THẤT BẠI: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Cause: " + e.getCause().getMessage());
            }
        }
    }
}
