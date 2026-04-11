package com.rapphim.view;

import javax.swing.SwingUtilities;
import com.rapphim.view.panels.Login;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}
