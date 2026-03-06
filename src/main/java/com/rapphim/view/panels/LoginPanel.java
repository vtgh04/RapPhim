package com.rapphim.view.panels;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;

public class LoginPanel extends JFrame {

    private int loginPanel_W  = 400;
    private int loginPanel_H  = 490;
    private int FIELD_W = 310;
    private int FIELD_H = 40;

    public LoginPanel() {
        setTitle("Cinema Manager Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Outer container (centers the loginPanel) ───────────────────────────
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(240, 242, 245));
        setContentPane(outer);

        // ── loginPanel panel — BoxLayout Y_AXIS, mọi child tự căn giữa ─────────
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(loginPanel_W, loginPanel_H));
        loginPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(35, 45, 30, 45)));
        outer.add(loginPanel);

        // ── Logo ──────────────────────────────────────────────────────────
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon logoIcon = loadIcon("images/icons/WelcomeLogo.png", 52, 52);
        if (logoIcon != null) logoLabel.setIcon(logoIcon);
        loginPanel.add(logoLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // ── Title ─────────────────────────────────────────────────────────
        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(new Color(30, 30, 35));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(FIELD_W, 30));
        loginPanel.add(title);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // ── Subtitle ──────────────────────────────────────────────────────
        JLabel subtitle = new JLabel("Sign in to Cinema Manager Pro", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(150, 155, 168));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setMaximumSize(new Dimension(FIELD_W, 22));
        loginPanel.add(subtitle);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 28)));

        // ── Account ID label ──────────────────────────────────────────────
        JLabel accountLabel = new JLabel("ACCOUNT ID");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        accountLabel.setForeground(new Color(50, 55, 65));
        accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountLabel.setMaximumSize(new Dimension(FIELD_W, 16));
        loginPanel.add(accountLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ── Account ID field ──────────────────────────────────────────────
        JPanel accountRow = createInputRow("images/icons/User.png");
        JTextField usernameField = new JTextField();
        usernameField.setBorder(null);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountRow.add(usernameField, BorderLayout.CENTER);
        accountRow.setMaximumSize(new Dimension(FIELD_W, FIELD_H));
        accountRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(accountRow);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // ── Password label ────────────────────────────────────────────────
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        passwordLabel.setForeground(new Color(50, 55, 65));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setMaximumSize(new Dimension(FIELD_W, 16));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ── Password field ────────────────────────────────────────────────
        JPanel passwordRow = createInputRow("images/icons/Password.png");
        JPasswordField pwField = new JPasswordField();
        pwField.setBorder(null);
        pwField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordRow.add(pwField, BorderLayout.CENTER);
        passwordRow.setMaximumSize(new Dimension(FIELD_W, FIELD_H));
        passwordRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(passwordRow);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 22)));

        // ── Sign In button ────────────────────────────────────────────────
        JButton signInBtn = new JButton("Sign In");
        signInBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signInBtn.setForeground(Color.WHITE);
        signInBtn.setBackground(new Color(220, 20, 20));
        signInBtn.setOpaque(true);
        signInBtn.setBorderPainted(false);
        signInBtn.setFocusPainted(false);
        signInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInBtn.setMinimumSize(new Dimension(FIELD_W, 42));
        signInBtn.setPreferredSize(new Dimension(FIELD_W, 42));
        signInBtn.setMaximumSize(new Dimension(FIELD_W, 42));
        loginPanel.add(signInBtn);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 18)));

    }

    /** Tạo một hàng input có icon bên trái. */
    private JPanel createInputRow(String iconPath) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        ImageIcon icon = loadIcon(iconPath, 20, 20);
        if (icon != null) panel.add(new JLabel(icon), BorderLayout.WEST);
        return panel;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}