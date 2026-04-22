package com.rapphim.service;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeStatus;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    public enum AuthError {
        INVALID_CREDENTIALS,
        ACCOUNT_INACTIVE,
        DATABASE_ERROR
    }

    public static class AuthException extends Exception {
        private final AuthError error;

        public AuthException(AuthError error, String message) {
            super(message);
            this.error = error;
        }

        public AuthError getError() {
            return error;
        }
    }

    private final EmployeeDAO employeeDAO;

    public AuthService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public AuthService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public Employee login(String username, String password) throws AuthException {
        Optional<Employee> opt;
        try {
            opt = employeeDAO.findByUsername(username);
        } catch (SQLException e) {
            System.err.println("[DB ERROR] " + e.getMessage());
            e.printStackTrace();
            throw new AuthException(AuthError.DATABASE_ERROR, e.getMessage());
        }

        if (opt.isEmpty()) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS,
                    "Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        Employee employee = opt.get();

        // 3. Kiểm tra trạng thái tài khoản
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new AuthException(AuthError.ACCOUNT_INACTIVE,
                    "Tài khoản đã bị khoá. Vui lòng liên hệ quản lý.");
        }

        // 4. Xác minh mật khẩu
        if (!password.equals(employee.getPassword())) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS,
                    "Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        // 5. Đăng nhập thành công
        return employee;
    }
}
