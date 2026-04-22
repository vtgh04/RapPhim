package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeRole;
import com.rapphim.model.enums.EmployeeStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO {

    // TimKiem
    private static final String SQL_FIND_BY_USERNAME = "SELECT employee_id, full_name, username, password," +
            "       role, status, phone, email" +
            "  FROM employees" +
            " WHERE username = ?";
    // TimKiem
    private static final String SQL_FIND_BY_ID = "SELECT employee_id, full_name, username, password," +
            "       role, status, phone, email" +
            "  FROM employees" +
            " WHERE employee_id = ?";
    // TimKiem
    private static final String SQL_FIND_ALL = "SELECT employee_id, full_name, username, password," +
            "       role, status, phone, email" +
            "  FROM employees" +
            " ORDER BY employee_id";
    // Them
    private static final String SQL_INSERT = "INSERT INTO employees " +
            "(employee_id, full_name, username, password, role, status, phone, email) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    // Cap nhat
    private static final String SQL_UPDATE = "UPDATE employees SET " +
            "full_name = ?, username = ?, role = ?, status = ?, phone = ?, email = ? " +
            "WHERE employee_id = ?";

    // Xoa
    private static final String SQL_DELETE = "DELETE FROM employees WHERE employee_id = ?";

    // Lay ma nhan vien tiep theo
    private static final String SQL_MAX_ID = "SELECT MAX(employee_id) AS max_id FROM employees";

    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(mapRow(rs));
            }
        }
        return employees;
    }

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

    public Optional<Employee> findById(String id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void insert(Employee employee) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, employee.getEmployeeId());
            ps.setNString(2, employee.getFullName());
            ps.setString(3, employee.getUsername());
            ps.setString(4, employee.getPassword());
            ps.setString(5, employee.getRole().getValue());
            ps.setString(6, employee.getStatus().getValue());
            ps.setString(7, employee.getPhone());
            ps.setString(8, employee.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(Employee employee) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setNString(1, employee.getFullName());
            ps.setString(2, employee.getUsername());
            ps.setString(3, employee.getRole().getValue());
            ps.setString(4, employee.getStatus().getValue());
            ps.setString(5, employee.getPhone());
            ps.setString(6, employee.getEmail());
            ps.setString(7, employee.getEmployeeId());
            ps.executeUpdate();
        }
    }

    public void delete(String employeeId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setString(1, employeeId);
            ps.executeUpdate();
        }
    }

    public String getNextEmployeeId() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_MAX_ID);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId != null && maxId.startsWith("EMP")) {
                    int num = Integer.parseInt(maxId.substring(3));
                    return String.format("EMP%03d", num + 1);
                }
            }
        }
        return "EMP001";
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getString("employee_id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("password"),
                EmployeeRole.fromString(rs.getString("role")),
                EmployeeStatus.fromString(rs.getString("status")),
                rs.getString("phone"),
                rs.getString("email"));
    }
}
