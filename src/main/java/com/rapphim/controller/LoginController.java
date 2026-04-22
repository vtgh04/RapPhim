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

public class LoginController {

    private final Login loginView;
    private final AuthService authService;

    public LoginController(Login loginView) {
        this.loginView = loginView;
        this.authService = new AuthService();
    }

    public void handleLogin(String username, String password, JButton signInBtn) {
        if (username.isBlank() || password.isBlank()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        signInBtn.setEnabled(false);
        signInBtn.setText("Đang đăng nhập...");

        new SwingWorker<Employee, Void>() {

            @Override
            protected Employee doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {

                signInBtn.setEnabled(true);
                signInBtn.setText("Sign In");

                try {
                    Employee employee = get();
                    openMainPage(employee);
                } catch (java.util.concurrent.ExecutionException ee) {
                    Throwable cause = ee.getCause();
                    if (cause instanceof AuthException ex) {
                        switch (ex.getError()) {
                            case ACCOUNT_INACTIVE -> showWarning(ex.getMessage());
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
