package com.rapphim.view;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.rapphim.view.panels.LoginPanel;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginPanel login = new LoginPanel();
            login.setVisible(true);
        });
    }
}