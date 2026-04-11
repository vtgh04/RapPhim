package com.rapphim.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
// import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.rapphim.controller.LoginController;

public class Login extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1;
    private final int FIELD_W = 310;
    private final int FIELD_H = 40;

    // Fields exposed to controller
    private JTextField usernameField;
    private JPasswordField pwField;
    private LoginController controller;

    public Login() {
        setTitle("Cinema Manager Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Color.WHITE);
        setContentPane(outer);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // ── BannerPanel (7 phần) ───────────────────────────
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(Color.BLACK);

        JLabel bannerLabel = new JLabel();
        ImageIcon bannerIcon = loadIcon("images/banners/LoadingScreen.png", (int) (1200 * 0.7), 650);
        if (bannerIcon != null) {
            bannerLabel.setIcon(bannerIcon);
            bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add title over the banner
            bannerLabel.setLayout(new GridBagLayout());
            JLabel titleBanner = new JLabel("RẠP PHIM CINEMAPRO");
            titleBanner.setFont(new Font("Arrial", Font.BOLD, 40));
            titleBanner.setForeground(Color.WHITE);

            GridBagConstraints titleGbc = new GridBagConstraints();
            titleGbc.gridx = 0;
            titleGbc.gridy = 0;
            titleGbc.insets = new Insets(0, 0, 80, 0);
            bannerLabel.add(titleBanner, titleGbc);
        }
        bannerPanel.add(bannerLabel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        outer.add(bannerPanel, gbc);

        // ── logPanelContainer (3 phần) ───────────────────────────
        JPanel logPanelContainer = new JPanel(new GridBagLayout());
        logPanelContainer.setBackground(Color.WHITE);

        // ── logPanel (Bên trong Container) ─────────────────────────
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ── Title ─────────────────────────────────────────────────────────
        JLabel title = new JLabel("Welcome Back", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(30, 30, 35));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setMaximumSize(new Dimension(FIELD_W, 35));
        logPanel.add(title);
        logPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ── Subtitle ──────────────────────────────────────────────────────
        JLabel subtitle = new JLabel("Sign in to access your account", SwingConstants.LEFT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(150, 155, 168));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setMaximumSize(new Dimension(FIELD_W, 25));
        logPanel.add(subtitle);
        logPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // ── Username label ──────────────────────────────────────────────
        JLabel accountLabel = new JLabel("Username");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountLabel.setForeground(new Color(50, 55, 65));
        accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountLabel.setMaximumSize(new Dimension(FIELD_W, 16));
        logPanel.add(accountLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ── Username field ──────────────────────────────────────────────
        JPanel accountRow = createInputRow("images/icons/User.png");
        usernameField = new JTextField();
        usernameField.setBorder(null);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountRow.add(usernameField, BorderLayout.CENTER);
        accountRow.setMaximumSize(new Dimension(FIELD_W, FIELD_H));
        accountRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(accountRow);
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Password label ────────────────────────────────────────────────
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(50, 55, 65));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setMaximumSize(new Dimension(FIELD_W, 16));
        logPanel.add(passwordLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ── Password field ────────────────────────────────────────────────
        JPanel passwordRow = createInputRow("images/icons/Password.png");
        pwField = new JPasswordField();
        pwField.setBorder(null);
        pwField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordRow.add(pwField, BorderLayout.CENTER);
        passwordRow.setMaximumSize(new Dimension(FIELD_W, FIELD_H));
        passwordRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(passwordRow);
        logPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // ── Sign In button ────────────────────────────────────────────────
        JButton signInBtn = new JButton("Sign In");
        signInBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signInBtn.setForeground(Color.WHITE);
        signInBtn.setBackground(new Color(220, 20, 20));
        signInBtn.setOpaque(true);
        signInBtn.setBorderPainted(false);
        signInBtn.setFocusPainted(false);
        signInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        signInBtn.setMinimumSize(new Dimension(FIELD_W, 45));
        signInBtn.setPreferredSize(new Dimension(FIELD_W, 45));
        signInBtn.setMaximumSize(new Dimension(FIELD_W, 45));
        logPanel.add(signInBtn);

        // ── Wire up LoginController ───────────────────────────────────────
        controller = new LoginController(this);

        // Click handler — DB chạy trên background thread trong controller
        signInBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(pwField.getPassword()).trim();
            controller.handleLogin(username, password, signInBtn);
        });

        // Allow pressing Enter in password field to trigger login
        pwField.addActionListener(e -> signInBtn.doClick());

        // Thêm logPanel vào giữa logPanelContainer
        logPanelContainer.add(logPanel);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        outer.add(logPanelContainer, gbc);

    }

    /**
     * Tạo một hàng input có icon bên trái.
     */
    private JPanel createInputRow(String iconPath) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        ImageIcon icon = loadIcon(iconPath, 20, 20);
        if (icon != null) {
            panel.add(new JLabel(icon), BorderLayout.WEST);
        }
        return panel;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) {
            return null;
        }
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
