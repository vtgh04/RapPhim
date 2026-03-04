package com.rapphim;

import com.formdev.flatlaf.FlatLightLaf;
import com.rapphim.view.panels.MainFrame;

import javax.swing.*;

/**
 * ============================================================
 * RapPhim - Hệ Thống Quản Lý Rạp Chiếu Phim
 * Entry point của ứng dụng
 * ============================================================
 *
 * @author   Team RapPhim
 * @version  1.0.0
 * @since    2026
 */
public class Main {

    public static void main(String[] args) {
        // Áp dụng FlatLaf Look and Feel (giao diện hiện đại)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Không thể áp dụng FlatLaf, dùng Look and Feel mặc định.");
        }

        // Chạy trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
