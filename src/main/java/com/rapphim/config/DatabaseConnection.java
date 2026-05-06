package com.rapphim.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String SERVER = "localhost\\SQLEXPRESS";
    private static final String PORT = "1433";
    private static final String DATABASE = "RapPhim";

    private static final String URL = "jdbc:sqlserver://" + SERVER + ":" + PORT + ";"
            + "databaseName=" + DATABASE + ";"
            + "encrypt=false;"
            + "trustServerCertificate=true;"
            + "loginTimeout=5;"
            + "connectRetryCount=0;";

    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    private static Connection instance;

    private DatabaseConnection() {
    }

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
