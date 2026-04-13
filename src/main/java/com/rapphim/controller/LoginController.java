package com.rapphim.controller;

import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeRole;
import com.rapphim.service.AuthService;
import com.rapphim.service.AuthService.AuthException;
import com.rapphim.view.panels.GeneralAdmin;
import com.rapphim.view.panels.GeneralStaff;
import com.rapphim.view.panels.Login;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Controller xử lý luồng đăng nhập.
 *
 * <p>
 * <b>Quan trọng:</b> Truy vấn DB được thực hiện trên
 * {@link SwingWorker} (background thread) để không chặn EDT,
 * tránh hiện tượng UI đóng băng.
 * </p>
 *
 * <ul>
 * <li>MANAGER → {@link GeneralAdmin}</li>
 * <li>STAFF → {@link GeneralStaff}</li>
 * </ul>
 */
public class LoginController {

    private final Login loginView;
    private final AuthService authService;

    public LoginController(Login loginView) {
        this.loginView = loginView;
        this.authService = new AuthService();
    }

    /**
     * Xử lý sự kiện nhấn nút "Sign In".
     * Gọi method này từ EDT; DB sẽ được truy vấn ở background thread.
     *
     * @param username  nội dung ô username
     * @param password  nội dung ô password
     * @param signInBtn nút Sign In để disable trong lúc chờ
     */
    public void handleLogin(String username, String password, JButton signInBtn) {
        // Validate input trước khi gọi DB
        if (username.isBlank() || password.isBlank()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        // Disable nút + đổi text để báo đang xử lý
        signInBtn.setEnabled(false);
        signInBtn.setText("Đang đăng nhập...");

        // Chạy DB call trên background thread (không block EDT)
        new SwingWorker<Employee, Void>() {

            // private AuthException authError;

            @Override
            protected Employee doInBackground() throws Exception {
                // ← chạy trên background thread
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                // ← chạy lại trên EDT khi doInBackground() xong
                signInBtn.setEnabled(true);
                signInBtn.setText("Sign In");

                try {
                    Employee employee = get(); // lấy kết quả hoặc ném exception
                    openMainPage(employee);
                } catch (java.util.concurrent.ExecutionException ee) {
                    Throwable cause = ee.getCause();
                    if (cause instanceof AuthException ex) {
                        switch (ex.getError()) {
                            case ACCOUNT_INACTIVE -> showWarning(ex.getMessage());
                            // Hiện lỗi SQL thật để debug
                            case DATABASE_ERROR -> showError(
                                    "Lỗi kết nối cơ sở dữ liệu:\n" + ex.getMessage());
                            default -> showError(ex.getMessage());
                        }
                    } else {
                        showError("Lỗi không xác định: " + cause.getMessage());
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
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
