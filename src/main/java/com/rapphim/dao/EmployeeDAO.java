package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeRole;
import com.rapphim.model.enums.EmployeeStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Data Access Object cho bảng {@code employees}.
 * <p>
 * Chỉ chứa các phương thức liên quan đến Employee.
 * Controller / Service không được truy cập JDBC trực tiếp.
 * </p>
 */
public class EmployeeDAO {

    // ── SQL Queries ──────────────────────────────────────────────────────────
    private static final String SQL_FIND_BY_USERNAME =
            "SELECT employee_id, full_name, username, password_hash," +
            "       role, status, phone, email" +
            "  FROM employees" +
            " WHERE username = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT employee_id, full_name, username, password_hash," +
            "       role, status, phone, email" +
            "  FROM employees" +
            " WHERE employee_id = ?";

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Tìm nhân viên theo {@code username}.
     *
     * @param username tên đăng nhập
     * @return {@link Optional} chứa Employee nếu tìm thấy, rỗng nếu không
     * @throws SQLException lỗi truy vấn DB
     */
    public Optional<Employee> findByUsername(String username) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Tìm nhân viên theo {@code employeeId}.
     *
     * @param id mã nhân viên
     * @return {@link Optional} chứa Employee nếu tìm thấy, rỗng nếu không
     * @throws SQLException lỗi truy vấn DB
     */
    public Optional<Employee> findById(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("password_hash"),
                EmployeeRole.fromString(rs.getString("role")),
                EmployeeStatus.fromString(rs.getString("status")),
                rs.getString("phone"),
                rs.getString("email")
        );
    }
}
