package com.rapphim.view.panels;

import com.rapphim.dao.HallDao;
import com.rapphim.model.CinemaHall;
import com.rapphim.model.Seat;
import com.rapphim.model.enums.SeatType;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HallPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color BG_PAGE = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;

    private static final Color SEAT_REGULAR = new Color(220, 30, 30);
    private static final Color SEAT_VIP = new Color(255, 185, 0);
    private static final Color SEAT_FAULTY = new Color(85, 90, 105);
    private static final Color SEAT_HOVER_REG = new Color(180, 10, 10);
    private static final Color SEAT_HOVER_VIP = new Color(210, 150, 0);

    private static final Color SCREEN_LEFT = new Color(255, 130, 130, 80);
    private static final Color SCREEN_RIGHT = new Color(255, 130, 130, 30);

    private static final Color ACCENT_RED = new Color(210, 20, 20);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_SUB = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_SEAT = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_ROW = new Font("Segoe UI", Font.BOLD, 12);

    private final HallDao hallDao = new HallDao();
    private List<CinemaHall> allHalls = new ArrayList<>();

    // Dữ liệu phòng hiện tại
    private CinemaHall currentHall;
    private boolean isUpdatingCombo = false;
    private final Map<String, Seat> seatMap = new HashMap<>();
    private final Set<Seat> modifiedSeats = new HashSet<>();

    private JComboBox<String> selectRoom;
    private JTextField txtHallName;
    private JTextField txtCap;
    private JComboBox<String> cmbType;
    private JPanel seatContainer;

    // Giá mặc định theo loại ghế (đọc từ DB / fallback)
    private JSpinner spnStdPrice;
    private JSpinner spnVipPrice;

    public HallPanel() {
        loadHallsFromDB();

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(BG_PAGE);
        contentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        contentPanel.add(buildNorth(), BorderLayout.NORTH);
        contentPanel.add(buildCenter(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getViewport().setBackground(BG_PAGE);

        add(scroll, BorderLayout.CENTER);
    }

    private void loadHallsFromDB() {
        try {
            allHalls = hallDao.findAllHalls();
        } catch (SQLException e) {
            e.printStackTrace();
            allHalls = new ArrayList<>();
        }
        if (!allHalls.isEmpty()) {
            currentHall = allHalls.get(0);
            loadSeatsForHall(currentHall.getHallId());
        }
    }

    private void loadSeatsForHall(String hallId) {
        seatMap.clear();
        modifiedSeats.clear();
        double stdPrice = 100_000;
        double vipPrice = 110_000;
        try {
            List<Seat> seats = hallDao.findSeatsByHall(hallId);
            for (Seat s : seats) {
                String key = String.valueOf(s.getRowChar()) + s.getColNumber();
                seatMap.put(key, s);
                // Lấy giá đại diện theo loại ghế (ghế đầu tiên của mỗi type)
                if (s.getSeatType() == SeatType.REGULAR && s.getSeatPrice() > 0) {
                    stdPrice = s.getSeatPrice();
                } else if (s.getSeatType() == SeatType.VIP && s.getSeatPrice() > 0) {
                    vipPrice = s.getSeatPrice();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Cập nhật spinners nếu đã khởi tạo
        if (spnStdPrice != null)
            spnStdPrice.setValue((int) stdPrice);
        if (spnVipPrice != null)
            spnVipPrice.setValue((int) vipPrice);
    }

    private JPanel buildNorth() {
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Left: tiêu đề
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        JLabel lblTitle = new JLabel("Phòng chiếu");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(new Color(25, 30, 40));
        JLabel lblDesc = new JLabel("Quản lý phòng chiếu và quản lý bảo trì.");
        lblDesc.setFont(FONT_SUB);
        lblDesc.setForeground(new Color(110, 115, 135));
        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(lblDesc);

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBox.setOpaque(false);

        String[] hallNames = allHalls.stream()
                .map(h -> h.getName() + " (" + h.getHallType() + ")")
                .toArray(String[]::new);
        if (hallNames.length == 0)
            hallNames = new String[] { "-- Không có dữ liệu --" };

        selectRoom = new JComboBox<>(hallNames);
        selectRoom.setFont(FONT_SUB);
        selectRoom.setPreferredSize(new Dimension(180, 38));
        selectRoom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        selectRoom.addActionListener(e -> onRoomChanged());

        rightBox.add(selectRoom);

        north.add(titleBox, BorderLayout.WEST);
        north.add(rightBox, BorderLayout.EAST);
        return north;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(18, 0));
        center.setOpaque(false);
        center.add(buildSeatMapWrapper(), BorderLayout.CENTER);
        center.add(buildSidebar(), BorderLayout.EAST);
        return center;
    }

    private JPanel buildSeatMapWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(WHITE);
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 235), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        // Screen bar
        JPanel screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, SCREEN_LEFT, w, 0, SCREEN_RIGHT));
                g2.fillRoundRect(40, 4, w - 80, h - 8, 8, 8);
                g2.dispose();
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g3.setColor(new Color(160, 60, 60));
                FontMetrics fm = g3.getFontMetrics();
                String txt = "Màn hình";
                g3.drawString(txt, (w - fm.stringWidth(txt)) / 2, h / 2 + 4);
                g3.dispose();
            }
        };
        screenPanel.setOpaque(false);
        screenPanel.setPreferredSize(new Dimension(0, 30));

        seatContainer = new JPanel(new BorderLayout());
        seatContainer.setOpaque(false);
        seatContainer.add(buildSeatGrid(), BorderLayout.CENTER);

        wrapper.add(screenPanel, BorderLayout.NORTH);
        wrapper.add(seatContainer, BorderLayout.CENTER);
        wrapper.add(buildLegend(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel buildSeatGrid() {
        int rows = (currentHall != null) ? currentHall.getTotalRows() : 0;
        int cols = (currentHall != null) ? currentHall.getTotalCols() : 0;

        JPanel grid = new JPanel(new GridLayout(rows, cols + 2, 5, 6));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(16, 10, 16, 10));

        for (int r = 1; r <= rows; r++) {
            char rowChar = (char) ('A' + r - 1);
            String rowStr = String.valueOf(rowChar);

            grid.add(rowLabel(rowStr));
            for (int c = 1; c <= cols; c++) {
                String key = rowStr + c;
                Seat seat = seatMap.get(key);
                if (seat != null) {
                    grid.add(buildSeatBtn(rowStr, c, seat));
                } else {
                    grid.add(new JLabel("")); // empty
                }
            }
            grid.add(rowLabel(rowStr));
        }
        return grid;
    }

    private JLabel rowLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(FONT_ROW);
        lbl.setForeground(new Color(150, 155, 168));
        lbl.setPreferredSize(new Dimension(20, 30));
        return lbl;
    }

    private JButton buildSeatBtn(String row, int col, Seat seat) {
        SeatType type = seat.getSeatType();
        boolean isBroken = seat.isBroken();
        Color base = isBroken ? SEAT_FAULTY : (type == SeatType.VIP ? SEAT_VIP : SEAT_REGULAR);
        Color fg = isBroken ? WHITE : (type == SeatType.VIP ? new Color(90, 60, 0) : WHITE);

        JButton btn = new JButton(isBroken ? "X" : String.valueOf(col)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_SEAT);
        btn.setForeground(fg);
        btn.setBackground(base);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(34, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(row + col + " – " + type.name());

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Color hover = seat.isBroken() ? SEAT_FAULTY
                        : (seat.getSeatType() == SeatType.VIP ? SEAT_HOVER_VIP : SEAT_HOVER_REG);
                btn.setBackground(hover);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Color currentBase = seat.isBroken() ? SEAT_FAULTY
                        : (seat.getSeatType() == SeatType.VIP ? SEAT_VIP : SEAT_REGULAR);
                btn.setBackground(currentBase);
                btn.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                boolean newStatus = !seat.isBroken();
                seat.setBroken(newStatus);
                modifiedSeats.add(seat);

                Color newBase = newStatus ? SEAT_FAULTY
                        : (seat.getSeatType() == SeatType.VIP ? SEAT_VIP : SEAT_REGULAR);
                Color newFg = newStatus ? WHITE : (seat.getSeatType() == SeatType.VIP ? new Color(90, 60, 0) : WHITE);

                btn.setText(newStatus ? "X" : String.valueOf(col));
                btn.setBackground(newBase);
                btn.setForeground(newFg);
                btn.repaint();
            }
        });
        return btn;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        legend.setOpaque(false);
        legend.add(legendItem(SEAT_REGULAR, "Phổ thông"));
        legend.add(legendItem(SEAT_VIP, "Vip"));
        legend.add(legendItem(SEAT_FAULTY, "Hỏng"));
        return legend;
    }

    private JPanel legendItem(Color color, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);
        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 2, 16, 16, 5, 5);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(16, 20));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(80, 85, 100));
        item.add(icon);
        item.add(lbl);
        return item;
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(240, 0));

        sidebar.add(buildHallDetailsCard());
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));
        sidebar.add(buildPricingCard());
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JPanel buildHallDetailsCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 235), 1, true),
                new EmptyBorder(18, 18, 18, 18)));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        hdr.setOpaque(false);
        hdr.setAlignmentX(LEFT_ALIGNMENT);
        hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Giới hạn chiều cao để không bị kéo giãn
        JLabel hdrLbl = new JLabel("Thông tin rạp");
        hdrLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hdrLbl.setForeground(new Color(25, 30, 40));
        hdr.add(hdrLbl);
        card.add(hdr);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // Hall Name
        card.add(sectionLabel("Tên phòng/rạp"));
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        txtHallName = new JTextField(currentHall != null ? currentHall.getName() : "");
        txtHallName.setFont(FONT_SUB);
        txtHallName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtHallName.setAlignmentX(LEFT_ALIGNMENT);
        txtHallName.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 225), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        card.add(txtHallName);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Capacity + Type row
        JPanel capRow = new JPanel(new GridLayout(1, 2, 10, 0));
        capRow.setOpaque(false);
        capRow.setAlignmentX(LEFT_ALIGNMENT);
        capRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel capBox = new JPanel();
        capBox.setLayout(new BoxLayout(capBox, BoxLayout.Y_AXIS));
        capBox.setOpaque(false);
        capBox.add(sectionLabel("Sức chứa"));
        capBox.add(Box.createRigidArea(new Dimension(0, 5)));
        int cap = (currentHall != null) ? currentHall.getTotalSeats() : 0;
        txtCap = new JTextField(String.valueOf(cap));
        txtCap.setFont(FONT_SUB);
        txtCap.setEditable(false);
        txtCap.setBackground(new Color(245, 246, 250));
        txtCap.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 225), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        capBox.add(txtCap);

        JPanel typeBox = new JPanel();
        typeBox.setLayout(new BoxLayout(typeBox, BoxLayout.Y_AXIS));
        typeBox.setOpaque(false);
        typeBox.add(sectionLabel("Loại phòng"));
        typeBox.add(Box.createRigidArea(new Dimension(0, 5)));
        cmbType = new JComboBox<>(new String[] { "2D", "3D", "IMAX" });
        cmbType.setFont(FONT_SUB);
        if (currentHall != null)
            cmbType.setSelectedItem(currentHall.getHallType());
        cmbType.setBorder(new LineBorder(new Color(210, 215, 225), 1, true));
        typeBox.add(cmbType);

        capRow.add(capBox);
        capRow.add(typeBox);
        card.add(capRow);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnSaveDetails = new JButton("Lưu thay đổi");
        btnSaveDetails.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveDetails.setBackground(ACCENT_RED);
        btnSaveDetails.setForeground(WHITE);
        btnSaveDetails.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSaveDetails.setBorderPainted(false);
        btnSaveDetails.setFocusPainted(false);
        btnSaveDetails.setOpaque(true);
        btnSaveDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSaveDetails.addActionListener(e -> saveModifications());
        btnSaveDetails.setAlignmentX(LEFT_ALIGNMENT);
        card.add(btnSaveDetails);

        return card;
    }

    private void saveModifications() {
        if (currentHall == null)
            return;
        boolean hasHallChange = false;
        boolean hasSeatChange = !modifiedSeats.isEmpty();

        String newName = txtHallName.getText().trim();
        String newType = cmbType.getSelectedItem().toString();

        if (!newName.equals(currentHall.getName()) || !newType.equals(currentHall.getHallType())) {

            if (!newName.equalsIgnoreCase(currentHall.getName())) {
                boolean isDuplicate = allHalls.stream().anyMatch(
                        h -> h.getName().equalsIgnoreCase(newName) && !h.getHallId().equals(currentHall.getHallId()));
                if (isDuplicate) {
                    JOptionPane.showMessageDialog(this, "Tên phòng chiếu đã tồn tại, vui lòng chọn tên khác!",
                            "Trùng lập", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            hasHallChange = true;
            try {
                hallDao.updateHallInfo(currentHall.getHallId(), newName, newType);
                currentHall.setName(newName);
                currentHall.setHallType(newType);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin rạp:\n" + e.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (hasSeatChange) {
            try {
                hallDao.updateSeatStatuses(modifiedSeats);
                modifiedSeats.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu ghế:\n" + e.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (hasHallChange || hasSeatChange) {
            JOptionPane.showMessageDialog(this, "Đã lưu thay đổi thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            if (hasHallChange) {
                isUpdatingCombo = true;
                int idx = selectRoom.getSelectedIndex();
                selectRoom.removeAllItems();
                for (CinemaHall h : allHalls) {
                    selectRoom.addItem(h.getName());
                }
                selectRoom.setSelectedIndex(idx);
                isUpdatingCombo = false;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không có thay đổi nào cần lưu.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel buildPricingCard() {
        // Lấy giá hiện tại từ spinner hoặc mặc định
        int currentStd = (spnStdPrice == null) ? 100_000 : (int) spnStdPrice.getValue();
        int currentVip = (spnVipPrice == null) ? 110_000 : (int) spnVipPrice.getValue();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 235), 1, true),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Giá ghế mặc định");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(new Color(20, 25, 40));
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Áp dụng cho tất cả ghế cùng loại.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(140, 145, 165));
        hint.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(hint);
        SpinnerNumberModel stdModel = new SpinnerNumberModel(currentStd, 0, 10_000_000, 5_000);
        spnStdPrice = new JSpinner(stdModel);
        spnStdPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spnStdPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnStdPrice.setAlignmentX(LEFT_ALIGNMENT);
        // Format editor không có dấu phẩy nhóm số
        JSpinner.NumberEditor stdEditor = new JSpinner.NumberEditor(spnStdPrice, "#,### VND");
        spnStdPrice.setEditor(stdEditor);

        SpinnerNumberModel vipModel = new SpinnerNumberModel(currentVip, 0, 10_000_000, 5_000);
        spnVipPrice = new JSpinner(vipModel);
        spnVipPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spnVipPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnVipPrice.setAlignmentX(LEFT_ALIGNMENT);
        JSpinner.NumberEditor vipEditor = new JSpinner.NumberEditor(spnVipPrice, "#,### VND");
        spnVipPrice.setEditor(vipEditor);

        card.add(pricingRow("Phổ thông", false, spnStdPrice));
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(pricingRow("Vip", true, spnVipPrice));
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnUpdate = new JButton("Cập nhật giá");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(ACCENT_RED);
        btnUpdate.setForeground(WHITE);
        btnUpdate.setOpaque(true);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUpdate.setAlignmentX(LEFT_ALIGNMENT);
        btnUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnUpdate.addActionListener(e -> handleUpdatePrices());

        card.add(btnUpdate);
        return card;
    }

    private void handleUpdatePrices() {
        if (currentHall == null)
            return;
        double stdPrice = ((Number) spnStdPrice.getValue()).doubleValue();
        double vipPrice = ((Number) spnVipPrice.getValue()).doubleValue();
        try {
            hallDao.updateSeatPriceByType(currentHall.getHallId(), SeatType.REGULAR, stdPrice);
            hallDao.updateSeatPriceByType(currentHall.getHallId(), SeatType.VIP, vipPrice);
            JOptionPane.showMessageDialog(this,
                    String.format("Đã cập nhật giá thành công!%n" +
                            "  Phổ thông: %,.0f VND%n" +
                            "  Vip:      %,.0f VND", stdPrice, vipPrice),
                    "Cập nhật giá ghế", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật giá:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel pricingRow(String label, boolean isVip, JSpinner spinner) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setBackground(new Color(247, 248, 250));
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(new Color(232, 234, 240), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(isVip ? new Color(185, 120, 0) : new Color(130, 135, 155));

        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(spinner, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(new Color(130, 135, 155));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private void onRoomChanged() {
        if (isUpdatingCombo)
            return;

        int idx = selectRoom.getSelectedIndex();
        if (idx < 0 || idx >= allHalls.size())
            return;

        currentHall = allHalls.get(idx);
        loadSeatsForHall(currentHall.getHallId());

        // Cập nhật sidebar hall details
        if (txtHallName != null)
            txtHallName.setText(currentHall.getName());
        if (txtCap != null)
            txtCap.setText(String.valueOf(currentHall.getTotalSeats()));
        if (cmbType != null)
            cmbType.setSelectedItem(currentHall.getHallType());

        // Rebuild grid ghế theo đúng số hàng/cột của phòng mới
        if (seatContainer != null) {
            seatContainer.removeAll();
            seatContainer.add(buildSeatGrid(), BorderLayout.CENTER);
            seatContainer.revalidate();
            seatContainer.repaint();
        }
    }
}
