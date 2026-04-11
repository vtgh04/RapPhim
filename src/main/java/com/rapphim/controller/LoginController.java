package com.rapphim.controller;

import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeRole;
import com.rapphim.service.AuthService;
import com.rapphim.service.AuthService.AuthException;
import com.rapphim.view.panels.GeneralAdmin;
import com.rapphim.view.panels.GeneralStaff;
import com.rapphim.view.panels.Login;

import javax.swing.JOptionPane;

/**
 * Controller xử lý luồng đăng nhập.
 *
 * <p>Nhận sự kiện từ {@link Login}, gọi {@link AuthService},
 * rồi điều hướng theo {@code role}:</p>
 * <ul>
 *   <li>MANAGER → {@link GeneralAdmin}</li>
 *   <li>STAFF   → {@link GeneralStaff}</li>
 * </ul>
 */
public class LoginController {

    private final Login       loginView;
    private final AuthService authService;

    public LoginController(Login loginView) {
        this.loginView   = loginView;
        this.authService = new AuthService();
    }

    /**
     * Xử lý sự kiện nhấn nút "Sign In".
     *
     * @param username nội dung ô username
     * @param password nội dung ô password
     */
    public void handleLogin(String username, String password) {
        // Validate input đơn giản phía UI
        if (username.isBlank() || password.isBlank()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            Employee employee = authService.login(username, password);
            openMainPage(employee);

        } catch (AuthException ex) {
            switch (ex.getError()) {
                case ACCOUNT_INACTIVE ->
                        showWarning(ex.getMessage());
                case DATABASE_ERROR ->
                        showError("Lỗi kết nối cơ sở dữ liệu.\nVui lòng kiểm tra lại cài đặt.");
                default ->
                        showError(ex.getMessage());
            }
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────
    private void openMainPage(Employee employee) {
        loginView.dispose();
        if (employee.getRole() == EmployeeRole.MANAGER) {
            GeneralAdmin.openAsFrame(employee);
        } else {
            GeneralStaff.openAsFrame(employee);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(loginView, message,
                "Đăng Nhập Thất Bại", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(loginView, message,
                "Tài Khoản Bị Khoá", JOptionPane.WARNING_MESSAGE);
    }
}
