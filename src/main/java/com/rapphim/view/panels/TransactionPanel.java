package com.rapphim.view.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionPanel extends JPanel {

    // ── Colors ──────────────────────────────────────────────────────────────
    private static final Color BG = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color DARK = new Color(15, 23, 42);
    private static final Color GRAY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color PURPLE = new Color(99, 102, 241);
    private static final Color BLUE = new Color(59, 130, 246);
    private static final Color GREEN = new Color(16, 185, 129);
    private static final Color RED = new Color(239, 68, 68);
    private static final Color SEP = new Color(241, 245, 249);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font F_BADGE = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font F_SUB = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_STLBL = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_STVAL = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font F_TH = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_CELL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_TAB = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BADGE2 = new Font("Segoe UI", Font.BOLD, 10);

    private static final Object[][] ALL_DATA = {
            { "ORD-99201", "4/16/2026, 5:30:00 PM", "Nguyễn Văn A", "MEM001", "CARD", "1.240.500 ₫", "COMPLETED" },
            { "ORD-99202", "4/16/2026, 6:15:00 PM", "Trần Thị B", "MEM002", "CASH", "660.000 ₫", "COMPLETED" },
            { "ORD-99203", "4/16/2026, 2:45:00 AM", "Lê Văn C", "MEM003", "VNPAY", "660.000 ₫", "REFUNDED" },
            { "ORD-99204", "4/15/2026, 8:00:00 PM", "Phạm Thị D", "MEM004", "CARD", "825.000 ₫", "COMPLETED" },
            { "ORD-99205", "4/15/2026,10:30:00 AM", "Hoàng Văn E", "MEM005", "CASH", "330.000 ₫", "COMPLETED" },
            { "ORD-99206", "4/14/2026, 3:15:00 PM", "Vũ Thị F", "MEM006", "MOMO", "495.000 ₫", "REFUNDED" },
            { "ORD-99207", "4/14/2026, 7:45:00 PM", "Đặng Văn G", "MEM007", "CARD", "990.000 ₫", "COMPLETED" },
    };

    private DefaultTableModel tableModel;
    private JTextField searchField;
    private String activeFilter = "ALL";
    private final List<Object[]> shownRows = new ArrayList<>();

    public TransactionPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 28, 24, 28));

        wrapper.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(buildStatRow(), BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);
        wrapper.add(body, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HEADER
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        // Left: badge + title + subtitle
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel badge = new JLabel("Hoá  đơn");
        badge.setFont(F_BADGE);
        badge.setForeground(PURPLE);

        JLabel title = new JLabel("Quản lý hoá đơn");
        title.setFont(F_TITLE);
        title.setForeground(DARK);

        JLabel sub = new JLabel("Xem lịch sử giao dịch, quản lý hoàn tiền và tạo báo cáo.");
        sub.setFont(F_SUB);
        sub.setForeground(GRAY);

        left.add(badge);
        left.add(title);
        left.add(sub);

        JButton btnExport = new JButton("Xuất CSV");
        btnExport.setFont(F_TAB);
        btnExport.setForeground(DARK);
        btnExport.setBackground(WHITE);
        btnExport.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Export CSV đang phát triển.", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(btnExport);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // STAT CARDS ROW
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildStatRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.add(makeStatCard(BLUE, "TOTAL SALES (TODAY)", "12.400.000", '0', '0'));
        row.add(makeStatCard(GREEN, "COMPLETED", "42 Orders", '0', '0'));
        row.add(makeStatCard(RED, "REFUNDED", "3 Orders", '0', '0'));
        return row;
    }

    private JPanel makeStatCard(Color iconColor, String label, String value, char c1, char c2) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        // Icon circle – custom painted so it always shows
        Color circleBg = new Color(
                Math.min(255, iconColor.getRed() + 210),
                Math.min(255, iconColor.getGreen() + 210),
                Math.min(255, iconColor.getBlue() + 210));
        JPanel circle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(circleBg);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(48, 48));
        circle.setMinimumSize(new Dimension(48, 48));
        circle.setMaximumSize(new Dimension(48, 48));

        String iconStr = (c2 == ' ') ? String.valueOf(c1) : new String(new char[] { c1, c2 });
        JLabel iconLbl = new JLabel(iconStr);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLbl.setForeground(iconColor);
        circle.add(iconLbl);

        // Text area
        JPanel text = new JPanel(new GridBagLayout());
        text.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        JLabel lLbl = new JLabel(label);
        lLbl.setFont(F_STLBL);
        lLbl.setForeground(GRAY);

        JLabel lVal = new JLabel(value);
        lVal.setFont(F_STVAL);
        lVal.setForeground(DARK);

        g.gridy = 0;
        text.add(lLbl, g);
        g.gridy = 1;
        g.insets = new Insets(6, 0, 0, 0);
        text.add(lVal, g);

        card.add(circle, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TABLE CARD
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        card.add(buildToolbar(), BorderLayout.NORTH);
        card.add(buildTable(), BorderLayout.CENTER);
        return card;
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────
    private JButton btnAll, btnComp, btnRef;

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Search
        searchField = new JTextField(24);
        searchField.setFont(F_CELL);
        searchField.setForeground(GRAY);
        searchField.setText("Tìm kiếm theo mã hoá đơn,tên nhân viên");
        searchField.setBackground(new Color(248, 250, 252));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        searchField.setPreferredSize(new Dimension(280, 40));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().startsWith("Tìm kiếm theo mã hoá đơn,tên nhân viên")) {
                    searchField.setText("");
                    searchField.setForeground(DARK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText("Tìm kiếm theo mã hoá đơn,tên nhân viên");
                    searchField.setForeground(GRAY);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
        });

        // Filter tabs
        btnAll = makeTab("Tất cả giao dịch", true);
        btnComp = makeTab("Hoàn thành", false);
        btnRef = makeTab("Đã hoàn tiền", false);

        btnAll.addActionListener(e -> {
            activeFilter = "ALL";
            refreshTabs(btnAll, btnComp, btnRef);
            applyFilter();
        });
        btnComp.addActionListener(e -> {
            activeFilter = "COMPLETED";
            refreshTabs(btnComp, btnAll, btnRef);
            applyFilter();
        });
        btnRef.addActionListener(e -> {
            activeFilter = "REFUNDED";
            refreshTabs(btnRef, btnAll, btnComp);
            applyFilter();
        });

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        tabs.setOpaque(false);
        tabs.add(btnAll);
        tabs.add(btnComp);
        tabs.add(btnRef);

        bar.add(searchField, BorderLayout.WEST);
        bar.add(tabs, BorderLayout.EAST);
        return bar;
    }

    private JButton makeTab(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(F_TAB);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyTabStyle(btn, active);
        return btn;
    }

    private void applyTabStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(DARK);
            btn.setForeground(WHITE);
            btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        } else {
            btn.setBackground(WHITE);
            btn.setForeground(GRAY);
            btn.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 14, 6, 14)));
        }
    }

    private void refreshTabs(JButton active, JButton... rest) {
        applyTabStyle(active, true);
        for (JButton b : rest)
            applyTabStyle(b, false);
    }

    // ── Table ────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = { "ORDER INFO", "CUSTOMER", "PAYMENT", "TOTAL", "STATUS", "ACTION" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        loadRows(ALL_DATA);

        JTable tbl = new JTable(tableModel);
        tbl.setFont(F_CELL);
        tbl.setRowHeight(62);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setBackground(WHITE);
        tbl.setFillsViewportHeight(true);
        tbl.setSelectionBackground(new Color(248, 250, 252));
        tbl.setSelectionForeground(DARK);

        JTableHeader th = tbl.getTableHeader();
        th.setFont(F_TH);
        th.setBackground(WHITE);
        th.setForeground(GRAY);
        th.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        th.setReorderingAllowed(false);

        int[] widths = { 165, 205, 105, 110, 130, 55 };
        for (int i = 0; i < widths.length; i++)
            tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        tbl.setDefaultRenderer(Object.class, new RowRenderer());

        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tbl.rowAtPoint(e.getPoint());
                int col = tbl.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5 && row < shownRows.size()) {
                    Object[] d = shownRows.get(row);
                    JOptionPane.showMessageDialog(TransactionPanel.this,
                            "Order: " + d[0] + "\nDate: " + d[1] + "\nCustomer: " + d[2],
                            "Order Detail", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private void loadRows(Object[][] data) {
        tableModel.setRowCount(0);
        shownRows.clear();
        for (Object[] r : data) {
            tableModel.addRow(new Object[] { r[0], r[2], r[4], r[5], r[6], "›" });
            shownRows.add(r);
        }
    }

    private void applyFilter() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.startsWith("\uD83D\uDD0D"))
            q = "";
        tableModel.setRowCount(0);
        shownRows.clear();
        for (Object[] r : ALL_DATA) {
            boolean okStatus = activeFilter.equals("ALL") || r[6].toString().equals(activeFilter);
            boolean okSearch = q.isEmpty()
                    || r[0].toString().toLowerCase().contains(q)
                    || r[2].toString().toLowerCase().contains(q);
            if (okStatus && okSearch) {
                tableModel.addRow(new Object[] { r[0], r[2], r[4], r[5], r[6], "›" });
                shownRows.add(r);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CELL RENDERER
    // ════════════════════════════════════════════════════════════════════════
    private class RowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {

            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(sel ? new Color(248, 250, 252) : WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, SEP));

            if (row < 0 || row >= shownRows.size())
                return cell;
            Object[] d = shownRows.get(row);
            // d = {orderId[0], date[1], name[2], memberId[3], payment[4], total[5],
            // status[6]}

            switch (col) {
                case 0 -> { // ORDER INFO
                    JPanel p = new JPanel();
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    p.setOpaque(false);
                    p.setBorder(new EmptyBorder(0, 4, 0, 0));

                    JLabel lId = new JLabel(d[0].toString());
                    lId.setFont(F_BOLD);
                    lId.setForeground(DARK);

                    JLabel lDt = new JLabel("\uD83D\uDCC5 " + d[1]);
                    lDt.setFont(F_SMALL);
                    lDt.setForeground(GRAY);

                    p.add(Box.createVerticalGlue());
                    p.add(lId);
                    p.add(lDt);
                    p.add(Box.createVerticalGlue());
                    cell.add(p, BorderLayout.CENTER);
                }

                case 1 -> { // CUSTOMER
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                    p.setOpaque(false);

                    // Avatar circle
                    JPanel av = new JPanel(new GridBagLayout()) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(226, 232, 240));
                            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    av.setOpaque(false);
                    av.setPreferredSize(new Dimension(34, 34));
                    JLabel avIcon = new JLabel("\uD83D\uDC64");
                    avIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                    avIcon.setForeground(GRAY);
                    av.add(avIcon);

                    JPanel info = new JPanel();
                    info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                    info.setOpaque(false);

                    JLabel lName = new JLabel(d[2].toString());
                    lName.setFont(F_BOLD);
                    lName.setForeground(DARK);

                    JLabel lMem = new JLabel("MEMBER: " + d[3]);
                    lMem.setFont(F_BADGE2);
                    lMem.setForeground(PURPLE);

                    info.add(lName);
                    info.add(lMem);

                    p.add(av);
                    p.add(info);
                    cell.add(p, BorderLayout.CENTER);
                }

                case 2 -> { // PAYMENT
                    String pm = d[4].toString();
                    Color pmColor = switch (pm) {
                        case "CARD" -> BLUE;
                        case "CASH" -> GREEN;
                        case "MOMO" -> new Color(168, 85, 247);
                        default -> new Color(234, 88, 12); // VNPAY
                    };
                    String pmIcon = switch (pm) {
                        case "CARD" -> "\uD83D\uDCB3";
                        case "CASH" -> "\uD83D\uDCB5";
                        default -> "\uD83D\uDCF1";
                    };
                    JLabel lbl = new JLabel(pmIcon + "  " + pm);
                    lbl.setFont(F_BOLD);
                    lbl.setForeground(pmColor);
                    lbl.setBorder(new EmptyBorder(0, 4, 0, 0));
                    cell.add(lbl, BorderLayout.CENTER);
                }

                case 3 -> { // TOTAL
                    JLabel lbl = new JLabel(d[5].toString());
                    lbl.setFont(F_BOLD);
                    lbl.setForeground(DARK);
                    lbl.setBorder(new EmptyBorder(0, 4, 0, 0));
                    cell.add(lbl, BorderLayout.CENTER);
                }

                case 4 -> { // STATUS badge
                    String status = d[6].toString();
                    boolean comp = status.equals("COMPLETED");
                    Color bgBadge = comp ? new Color(220, 252, 231) : new Color(254, 226, 226);
                    Color fgBadge = comp ? new Color(22, 163, 74) : RED;

                    JLabel badge = new JLabel((comp ? "\u2713" : "\u26A0") + "  " + status) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(bgBadge);
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    badge.setFont(F_BADGE2);
                    badge.setForeground(fgBadge);
                    badge.setOpaque(false);
                    badge.setBorder(new EmptyBorder(4, 10, 4, 10));

                    JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                    wrap.setOpaque(false);
                    wrap.add(badge);
                    cell.add(wrap, BorderLayout.CENTER);
                }

                default -> { // ACTION arrow
                    JLabel arrow = new JLabel("\u203A");
                    arrow.setFont(new Font("Segoe UI", Font.BOLD, 22));
                    arrow.setForeground(new Color(203, 213, 225));
                    arrow.setHorizontalAlignment(SwingConstants.CENTER);
                    cell.add(arrow, BorderLayout.CENTER);
                }
            }
            return cell;
        }
    }
}
