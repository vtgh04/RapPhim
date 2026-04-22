package com.rapphim.view.panels;

import com.rapphim.model.Employee;
// import com.rapphim.view.panels.EmployeePanel;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class GeneralAdmin extends JPanel {

    private static final long serialVersionUID = 1L;
    // ── Design tokens
    private static final Color PRIMARY_RED = new Color(220, 20, 20);
    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color SIDEBAR_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_SECONDARY = new Color(150, 155, 168);
    private static final Color BORDER_COLOR = new Color(228, 228, 228);

    private static final Font FONT_PLAIN_13 = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD_11 = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_BOLD_26 = new Font("Segoe UI", Font.BOLD, 26);

    private static final int SIDEBAR_W = 200;

    private JButton activeNavBtn = null;
    private final JPanel rightPanel;
    private String loggedInName = "";
    private Employee currentEmployee = null;

    /** Constructor mặc định (dùng cho test UI độc lập). */
    public GeneralAdmin() {
        this(null);
    }

    /**
     * Constructor nhận Employee sau khi đăng nhập thành công.
     *
     * @param employee nhân viên đã xác thực (null = chế độ test)
     */
    public GeneralAdmin(Employee employee) {
        this.currentEmployee = employee;
        if (employee != null) {
            this.loggedInName = employee.getFullName();
        }
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * Mở Admin Dashboard trong JFrame mới.
     * Dùng bởi {@link com.rapphim.controller.LoginController}.
     */
    public static void openAsFrame(Employee employee) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ứng dụng bán vé tại rạp chiếu phim");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1600, 900);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new GeneralAdmin(employee));
            frame.setVisible(true);
        });
    }

    private JPanel createLeftPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // ── Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setBackground(SIDEBAR_BG);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setBorder(new EmptyBorder(25, 15, 20, 15));

        ImageIcon logoIcon = loadIcon("images/icons/cinema.png", 46, 46);
        if (logoIcon != null) {
            JLabel iconLabel = new JLabel(logoIcon);
            logoPanel.add(iconLabel);
            logoPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(SIDEBAR_BG);

        JLabel brandName = new JLabel("CinePro");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandName.setForeground(new Color(0, 51, 102));
        brandName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("Quản lý rạp phim");
        brandSub.setFont(new Font("Consolas", Font.PLAIN, 12));
        brandSub.setForeground(new Color(110, 150, 190));
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(brandName);
        textPanel.add(brandSub);

        logoPanel.add(textPanel);
        logoPanel.add(Box.createHorizontalGlue()); // Đẩy các icon/text sang lề trái
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Navigation Buttons
        JButton dashBtn = createNavButton("Dashboard", "images/icons/dashboard.png", true);
        activeNavBtn = dashBtn;
        sidebar.add(dashBtn);
        sidebar.add(createNavButton("Sales & POS", "images/icons/sales.png", false));
        sidebar.add(createNavButton("Transactions", "images/icons/transactions.png", false));
        sidebar.add(createNavButton("Members", "images/icons/members.png", false));
        sidebar.add(createNavButton("Movies", "images/icons/Movies.png", false));
        sidebar.add(createNavButton("Showtimes", "images/icons/Showtimes.png", false));
        sidebar.add(createNavButton("Halls & Seats", "images/icons/hall.png", false));
        sidebar.add(createNavButton("Employees", "images/icons/employees.png", false));

        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // ── F&B Management header
        JLabel fbHeader = new JLabel("F&B MANAGEMENT");
        fbHeader.setFont(FONT_BOLD_11);
        fbHeader.setForeground(TEXT_SECONDARY);
        fbHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        fbHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        sidebar.add(fbHeader);

        sidebar.add(createNavButton("Products", "images/icons/Product.png", false));

        // ── Spacer (đẩy các nút xuống cuối)
        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createNavButton("Settings", "images/icons/Setting.png", false));
        sidebar.add(createNavButton("Logout", "images/icons/logout.png", false));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        return sidebar;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);

        String name = loggedInName.isBlank() ? "" : ", " + loggedInName + "!";
        JLabel welcomeLabel = new JLabel("Welcome back" + name);
        welcomeLabel.setFont(FONT_BOLD_26);
        welcomeLabel.setForeground(TEXT_PRIMARY);

        panel.add(welcomeLabel);
        return panel;
    }

    private JButton createNavButton(String text, String iconPath, boolean isDashboardActive) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_PLAIN_13);
        btn.setForeground(isDashboardActive ? Color.WHITE : TEXT_PRIMARY);
        btn.setBackground(isDashboardActive ? PRIMARY_RED : SIDEBAR_BG);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setBorder(new EmptyBorder(12, 20, 12, 10)); // Top, Left, Bottom, Right

        // Wrap button in a panel to center constrain its width
        Dimension btnSize = new Dimension(SIDEBAR_W - 30, 42);
        btn.setMinimumSize(btnSize);
        btn.setPreferredSize(btnSize);
        btn.setMaximumSize(btnSize);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon icon = loadIcon(iconPath, 18, 18);
        if (icon != null) {
            btn.setIcon(isDashboardActive ? tintIcon(icon, Color.WHITE) : icon);
        }

        // Action Listener handling for all buttons
        btn.addActionListener((ActionEvent e) -> {
            if (text.equals("Logout")) {
                java.awt.Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
                SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                return;
            }

            // Update active button state
            if (activeNavBtn != null && activeNavBtn != btn) {
                activeNavBtn.setBackground(SIDEBAR_BG);
                activeNavBtn.setForeground(TEXT_PRIMARY);
                ImageIcon origIcon = loadIcon(iconPathForButton(activeNavBtn.getText()), 18, 18);
                if (origIcon != null) {
                    activeNavBtn.setIcon(origIcon);
                }
            }

            btn.setBackground(PRIMARY_RED);
            btn.setForeground(Color.WHITE);
            if (icon != null) {
                btn.setIcon(tintIcon(icon, Color.WHITE));
            }
            activeNavBtn = btn;

            // Handle RightPanel logic based on button clicked
            handleNavigation(text);
        });

        // Hover effect listener
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn) {
                    btn.setBackground(new Color(245, 245, 248));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });

        return btn;
    }

    /**
     * Display RightPanel content based on the navigation item clicked.
     */
    private void handleNavigation(String page) {
        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        switch (page) {
            case "Dashboard" -> {
                rightPanel.setLayout(new GridBagLayout());
                String name = loggedInName.isBlank() ? "!" : ", " + loggedInName + "!";
                JLabel welcomeLabel = new JLabel("Welcome back" + name);
                welcomeLabel.setFont(FONT_BOLD_26);
                welcomeLabel.setForeground(TEXT_PRIMARY);
                rightPanel.add(welcomeLabel);
            }
            case "Employees" -> rightPanel.add(new EmployeePanel(), BorderLayout.CENTER);
            case "Movies" -> rightPanel.add(new MoviePanel(), BorderLayout.CENTER);
            case "Halls & Seats" -> rightPanel.add(new HallPanel(), BorderLayout.CENTER);
            case "Sales & POS" -> rightPanel.add(createPlaceholderPanel(
                    "Sales & POS", "images/icons/sales.png",
                    "Quản lý bán vé và điểm bán hàng tại quầy."), BorderLayout.CENTER);
            case "Transactions" -> rightPanel.add(createPlaceholderPanel(
                    "Transactions", "images/icons/transactions.png",
                    "Xem lịch sử và thống kê giao dịch."), BorderLayout.CENTER);
            case "Members" -> rightPanel.add(createPlaceholderPanel(
                    "Members", "images/icons/members.png",
                    "Quản lý hội viên và chương trình thành viên."), BorderLayout.CENTER);
            case "Showtimes" -> rightPanel.add(createPlaceholderPanel(
                    "Showtimes", "images/icons/Showtimes.png",
                    "Lập lịch và quản lý suất chiếu phim."), BorderLayout.CENTER);
            case "Products" -> rightPanel.add(createPlaceholderPanel(
                    "Products", "images/icons/Product.png",
                    "Quản lý sản phẩm F&B: bắp rang, nước uống, combo."), BorderLayout.CENTER);
            case "Settings" -> rightPanel.add(new SettingPanel(currentEmployee), BorderLayout.CENTER);
            default -> {
                rightPanel.setLayout(new GridBagLayout());
                JLabel pageLabel = new JLabel(page + " Page");
                pageLabel.setFont(FONT_BOLD_26);
                pageLabel.setForeground(TEXT_PRIMARY);
                rightPanel.add(pageLabel);
            }
        }

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    /**
     * Tạo một panel giữ chỗ (placeholder) đẹp cho các trang chưa triển khai.
     */
    private JPanel createPlaceholderPanel(String title, String iconPath, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(60, 0, 0, 0));

        // Icon lớn
        ImageIcon bigIcon = loadIcon(iconPath, 64, 64);
        if (bigIcon != null) {
            JLabel iconLbl = new JLabel(bigIcon);
            iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(iconLbl);
            panel.add(Box.createRigidArea(new Dimension(0, 24)));
        }

        // Tiêu đề
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_BOLD_26);
        titleLbl.setForeground(TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Mô tả
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLbl.setForeground(TEXT_SECONDARY);
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Badge "Đang phát triển"
        JLabel badge = new JLabel("🚧  Tính năng đang được phát triển");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(new Color(180, 100, 0));
        badge.setOpaque(true);
        badge.setBackground(new Color(255, 237, 213));
        badge.setBorder(new EmptyBorder(10, 24, 10, 24));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(badge);

        // Bọc trong wrapper để căn giữa dọc
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.add(panel);
        return wrapper;
    }

    private String iconPathForButton(String label) {
        return switch (label) {
            case "Dashboard" -> "images/icons/dashboard.png";
            case "Sales & POS" -> "images/icons/sales.png";
            case "Transactions" -> "images/icons/transactions.png";
            case "Members" -> "images/icons/members.png";
            case "Movies" -> "images/icons/Movies.png";
            case "Showtimes" -> "images/icons/Showtimes.png";
            case "Halls & Seats" -> "images/icons/hall.png";
            case "Employees" -> "images/icons/employees.png";
            case "Products" -> "images/icons/Product.png";
            case "Settings" -> "images/icons/Setting.png";
            case "Logout" -> "images/icons/logout.png";
            default -> "";
        };
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) {
            return null;
        }
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private ImageIcon tintIcon(ImageIcon icon, Color color) {
        Image img = icon.getImage();
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.setColor(color);
        g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g2.dispose();
        return new ImageIcon(bi);
    }

}
