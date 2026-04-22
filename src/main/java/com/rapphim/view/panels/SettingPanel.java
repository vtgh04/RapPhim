package com.rapphim.view.panels;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.dao.HallDao;
import com.rapphim.dao.MovieDAO;
import com.rapphim.model.Employee;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SettingPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color C_PRIMARY = new Color(25, 30, 40);
    private static final Color C_SECOND = new Color(110, 115, 135);
    private static final Color C_BORDER = new Color(225, 228, 235);
    private static final Color C_SEP = new Color(238, 240, 245);
    private static final Color C_ICON_BG = new Color(235, 237, 255);
    private static final Color C_ICON_FG = new Color(99, 102, 241);
    private static final Color C_HOVER = new Color(248, 249, 255);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font F_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font F_ITEM = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_DESC = new Font("Segoe UI", Font.PLAIN, 12);

    private final Employee employee;
    private int totalEmployees = 0;
    private int totalHalls = 0;
    private int totalMovies = 0;

    // CardLayout for sub-page navigation
    private CardLayout cardLayout;
    private JPanel cardHost;

    public SettingPanel() {
        this(null);
    }

    public SettingPanel(Employee employee) {
        this.employee = employee;
        loadStats();
        buildUI();
    }

    private void loadStats() {
        try {
            totalEmployees = new EmployeeDAO().findAll().size();
        } catch (Exception ignored) {
        }
        try {
            totalHalls = new HallDao().findAllHalls().size();
        } catch (Exception ignored) {
        }
        try {
            totalMovies = new MovieDAO().findAll().size();
        } catch (Exception ignored) {
        }
    }

    private void buildUI() {
        setBackground(BG);
        cardLayout = new CardLayout();
        cardHost = new JPanel(cardLayout);
        cardHost.setBackground(BG);

        // ── Main settings page ─────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("Settings");
        lblTitle.setFont(F_TITLE);
        lblTitle.setForeground(C_PRIMARY);

        JLabel lblSub = new JLabel("Quản lý tài khoản và cấu hình hệ thống.");
        lblSub.setFont(F_SUBTITLE);
        lblSub.setForeground(C_SECOND);

        JPanel hdrText = new JPanel();
        hdrText.setLayout(new BoxLayout(hdrText, BoxLayout.Y_AXIS));
        hdrText.setPreferredSize(new Dimension(500, 100));
        hdrText.setOpaque(false);
        hdrText.add(lblTitle);
        hdrText.add(Box.createRigidArea(new Dimension(0, 3)));
        hdrText.add(lblSub);
        header.add(hdrText, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        body.add(buildProfileCard());
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(buildConfigCard());

        root.add(body, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        // ── SecurityProfilePanel ───────────────────────────────────────
        SecurityProfilePanel securityPanel = new SecurityProfilePanel(employee, () -> navigate("main"));

        cardHost.add(scroll, "main");
        cardHost.add(securityPanel, "security");

        setLayout(new BorderLayout());
        add(cardHost, BorderLayout.CENTER);
    }

    /** Navigate between setting sub-pages. */
    private void navigate(String card) {
        cardLayout.show(cardHost, card);
    }

    private JPanel buildProfileCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        profileRow.setOpaque(false);
        profileRow.setBorder(new EmptyBorder(18, 24, 18, 24));
        profileRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Avatar circle
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(243, 244, 246));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        Dimension avSize = new Dimension(52, 52);
        avatar.setPreferredSize(avSize);
        avatar.setMinimumSize(avSize);
        avatar.setMaximumSize(avSize);

        ImageIcon avIcon = loadIcon("images/icons/avatar.png", 26, 26);
        JLabel ico = new JLabel(avIcon != null ? avIcon : null);
        if (avIcon == null) {
            ico.setText("\uD83D\uDC64");
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        }
        ico.setForeground(new Color(107, 114, 128));
        avatar.add(ico);

        // Info stacked vertically centered
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        String nameText = (employee != null && employee.getFullName() != null)
                ? employee.getFullName()
                : "Manager";
        String roleVal = (employee != null && employee.getRole() != null)
                ? employee.getRole().toString()
                : "MANAGER";
        String idVal = (employee != null && employee.getEmployeeId() != null)
                ? employee.getEmployeeId()
                : "";

        JLabel lblName = new JLabel(nameText);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setForeground(new Color(17, 24, 39));

        JLabel lblRole = new JLabel("Role: " + roleVal + "  (" + idVal + ")");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(107, 114, 128));

        info.add(lblName);
        info.add(Box.createRigidArea(new Dimension(0, 3)));
        info.add(lblRole);

        profileRow.add(avatar);
        profileRow.add(info);
        card.add(profileRow);

        // Divider
        card.add(buildSep());

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3));
        statsRow.setOpaque(false);
        statsRow.setBorder(new EmptyBorder(0, 0, 0, 0));
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        statsRow.add(buildStatCell(" Nhân viên", String.valueOf(totalEmployees), false));
        statsRow.add(buildStatCell(" Phòng chiếu", String.valueOf(totalHalls), true));
        statsRow.add(buildStatCell(" Phim", String.valueOf(totalMovies), false));
        card.add(statsRow);

        return card;
    }

    private JPanel buildStatCell(String label, String value, boolean bordered) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setBackground(new Color(249, 250, 251));
        cell.setBorder(new CompoundBorder(
                new MatteBorder(1, bordered ? 1 : 0, 0, bordered ? 1 : 0, C_SEP),
                new EmptyBorder(14, 20, 14, 20)));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lVal.setForeground(C_PRIMARY);
        lVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lLbl = new JLabel(label);
        lLbl.setFont(F_DESC);
        lLbl.setForeground(C_SECOND);
        lLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(lVal);
        inner.add(Box.createRigidArea(new Dimension(0, 2)));
        inner.add(lLbl);
        cell.add(inner);
        return cell;
    }

    private JPanel buildConfigCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new LineBorder(C_BORDER, 1, true));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        card.add(buildSectionLabel("SYSTEM CONFIGURATION"));

        SettingItem[] items = {
                new SettingItem("images/icons/baomat.png", "Bảo mật", "Cài đặt tài khoản và mật khẩu",
                        "security"),

        };

        for (int i = 0; i < items.length; i++) {
            card.add(buildSettingRow(items[i]));
            if (i < items.length - 1)
                card.add(buildSep());
        }

        return card;
    }

    private JPanel buildSectionLabel(String text) {
        JPanel w = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 14));
        w.setOpaque(false);
        w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_SECTION);
        lbl.setForeground(C_SECOND);
        w.add(lbl);
        return w;
    }

    private JPanel buildSettingRow(SettingItem item) {
        JPanel row = new JPanel(new BorderLayout(16, 0)) {
            @Override
            public boolean isOpaque() {
                return true;
            }
        };
        row.setBackground(WHITE);
        row.setBorder(new EmptyBorder(16, 24, 16, 24));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(C_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                navigate(item.key);
            }
        });

        // Icon box
        JPanel iconBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ICON_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        Dimension boxSize = new Dimension(42, 42);
        iconBox.setPreferredSize(boxSize);
        iconBox.setMinimumSize(boxSize);
        iconBox.setMaximumSize(boxSize);

        ImageIcon itemIcon = (item.iconPath != null && !item.iconPath.isEmpty())
                ? loadIcon(item.iconPath, 24, 24)
                : null;
        JLabel emojiLbl = new JLabel(itemIcon != null ? itemIcon : null);
        if (itemIcon == null) {
            emojiLbl.setText(item.iconPath); // Fallback to iconPath text if icon not found
            emojiLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        }
        emojiLbl.setForeground(C_ICON_FG);
        iconBox.add(emojiLbl);

        // Text
        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);

        JLabel lTitle = new JLabel(item.title);
        lTitle.setFont(F_ITEM);
        lTitle.setForeground(C_PRIMARY);

        JLabel lDesc = new JLabel(item.desc);
        lDesc.setFont(F_DESC);
        lDesc.setForeground(C_SECOND);

        textBox.add(lTitle);
        textBox.add(Box.createRigidArea(new Dimension(0, 3)));
        textBox.add(lDesc);

        // Arrow
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 22));
        arrow.setForeground(new Color(190, 195, 210));

        row.add(iconBox, BorderLayout.WEST);
        row.add(textBox, BorderLayout.CENTER);
        row.add(arrow, BorderLayout.EAST);

        return row;
    }

    private JPanel buildSep() {
        JPanel sep = new JPanel();
        sep.setBackground(C_SEP);
        sep.setOpaque(true);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        return sep;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            java.net.URL imgUrl = getClass().getClassLoader().getResource(path);
            if (imgUrl != null) {
                Image scaled = new ImageIcon(imgUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private record SettingItem(String iconPath, String title, String desc, String key) {
    }
}
