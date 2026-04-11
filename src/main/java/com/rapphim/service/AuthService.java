package com.rapphim.service;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeStatus;


import java.sql.SQLException;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ đăng nhập.
 *
 * <p><b>Logic chuẩn theo đặc tả:</b></p>
 * <ol>
 *   <li>Tìm nhân viên theo {@code username}.</li>
 *   <li>Nếu không tồn tại → ném {@link AuthException} với INVALID_CREDENTIALS.</li>
 *   <li>Nếu {@code status != ACTIVE} → ném {@link AuthException} với ACCOUNT_INACTIVE.</li>
 *   <li>Xác minh mật khẩu; sai → ném {@link AuthException} với INVALID_CREDENTIALS.</li>
 *   <li>Trả về {@link Employee} đã xác thực.</li>
 * </ol>
 *
 * <p>Caller (Controller) sẽ kiểm tra {@code employee.getRole()} để điều hướng trang.</p>
 */
public class AuthService {

    /**
     * Kết quả / lý do lỗi đăng nhập.
     */
    public enum AuthError {
        /** Tên đăng nhập hoặc mật khẩu sai. */
        INVALID_CREDENTIALS,
        /** Tài khoản tồn tại nhưng đang bị khoá (INACTIVE). */
        ACCOUNT_INACTIVE,
        /** Lỗi kết nối cơ sở dữ liệu. */
        DATABASE_ERROR
    }

    /**
     * Exception ném khi đăng nhập thất bại.
     */
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

    // ── Dependencies ─────────────────────────────────────────────────────────
    private final EmployeeDAO employeeDAO;

    public AuthService() {
        this.employeeDAO = new EmployeeDAO();
    }

    /** Constructor cho unit test (dependency injection). */
    public AuthService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Thực hiện đăng nhập.
     *
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return {@link Employee} đã được xác thực
     * @throws AuthException   khi đăng nhập thất bại (xem {@link AuthError})
     */
    public Employee login(String username, String password) throws AuthException {
        // 1. Tìm theo username
        Optional<Employee> opt;
        try {
            opt = employeeDAO.findByUsername(username);
        } catch (SQLException e) {
            // In lỗi thật ra Eclipse Console để debug
            System.err.println("[DB ERROR] " + e.getMessage());
            e.printStackTrace();
            throw new AuthException(AuthError.DATABASE_ERROR, e.getMessage());
        }

        // 2. Username không tồn tại
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
