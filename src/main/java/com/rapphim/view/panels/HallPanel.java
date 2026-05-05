package com.rapphim.view.panels;

import com.rapphim.dao.HallDao;
import com.rapphim.model.CinemaHall;
import com.rapphim.model.Seat;
import com.rapphim.model.enums.CinemaHallStatus;
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

    private CinemaHall currentHall;
    private boolean isUpdatingCombo = false;
    private final Map<String, Seat> seatMap = new HashMap<>();
    private final Set<Seat> modifiedSeats = new HashSet<>();

    private JComboBox<String> selectRoom;
    private JTextField txtHallName;
    private JTextField txtCap;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbStatus;
    private JPanel seatContainer;

    private JSpinner spnStdFactor;
    private JSpinner spnVipFactor;

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
        double stdFactor = 1.0;
        double vipFactor = 1.5;
        try {
            List<Seat> seats = hallDao.findSeatsByHall(hallId);
            for (Seat s : seats) {
                String key = String.valueOf(s.getRowChar()) + s.getColNumber();
                seatMap.put(key, s);
                if (s.getSeatType() == SeatType.REGULAR && s.getSeatFactor() > 0) {
                    stdFactor = s.getSeatFactor();
                } else if (s.getSeatType() == SeatType.VIP && s.getSeatFactor() > 0) {
                    vipFactor = s.getSeatFactor();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (spnStdFactor != null)
            spnStdFactor.setValue(stdFactor);
        if (spnVipFactor != null)
            spnVipFactor.setValue(vipFactor);
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
        Color fg = isBroken ? WHITE : (type == SeatType.VIP ? new Color(90, 60, 0) : WHITE);

        // Nạp icon tương ứng
        String iconPath = isBroken ? "/images/icons/wrench.png"
                : (type == SeatType.VIP ? "/images/icons/Yellow Chair.png" : "/images/icons/chair.png");
        java.net.URL imgUrl = getClass().getResource(iconPath);
        final Image seatImg = (imgUrl != null) ? new ImageIcon(imgUrl).getImage() : null;

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (seatImg != null) {
                    // Cân đối kích thước ảnh không bị méo (giữ aspect ratio)
                    int imgW = seatImg.getWidth(null);
                    int imgH = seatImg.getHeight(null);
                    if (imgW > 0 && imgH > 0) {
                        double scale = Math.min((double) getWidth() / imgW, (double) getHeight() / imgH);
                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);
                        int drawX = (getWidth() - drawW) / 2;
                        int drawY = (getHeight() - drawH) / 2;

                        // Nếu đang hover và không hỏng, vẽ hiệu ứng (tùy chọn)
                        if (getModel().isRollover() && !isBroken) {
                            g2.setColor(type == SeatType.VIP ? SEAT_HOVER_VIP : SEAT_HOVER_REG);
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                        }

                        // Vẽ ảnh ghế
                        g2.drawImage(seatImg, drawX, drawY, drawW, drawH, this);
                    } else {
                        g2.drawImage(seatImg, 0, 0, getWidth(), getHeight(), this);
                    }
                } else {
                    // Fallback nếu không tìm thấy ảnh
                    Color base = isBroken ? SEAT_FAULTY : (type == SeatType.VIP ? SEAT_VIP : SEAT_REGULAR);
                    if (getModel().isRollover()) {
                        base = isBroken ? SEAT_FAULTY : (type == SeatType.VIP ? SEAT_HOVER_VIP : SEAT_HOVER_REG);
                    }
                    g2.setColor(base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                // Vẽ text (số ghế hoặc không vẽ nếu là ghế hỏng)
                if (!isBroken) {
                    String text = String.valueOf(col);
                    g2.setFont(FONT_SEAT);
                    g2.setColor(fg);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(text, x, y - 2); // Điều chỉnh vị trí Y một chút để cân đối với icon
                }

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(row + col + " – " + type.name());
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                boolean newStatus = !seat.isBroken();
                seat.setBroken(newStatus);
                modifiedSeats.add(seat);
                Container parent = btn.getParent();
                if (parent != null) {
                    int index = -1;
                    for (int i = 0; i < parent.getComponentCount(); i++) {
                        if (parent.getComponent(i) == btn) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        parent.remove(index);
                        parent.add(buildSeatBtn(row, col, seat), index);
                        parent.revalidate();
                        parent.repaint();
                    }
                }
            }
        });
        return btn;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        legend.setOpaque(false);
        legend.add(legendItemImage("/images/icons/chair.png", "Phổ thông"));
        legend.add(legendItemImage("/images/icons/Yellow Chair.png", "Vip"));
        legend.add(legendItemImage("/images/icons/wrench.png", "Hỏng"));
        return legend;
    }

    private JPanel legendItemImage(String iconPath, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);

        java.net.URL imgUrl = getClass().getResource(iconPath);
        final Image seatImg = (imgUrl != null) ? new ImageIcon(imgUrl).getImage() : null;

        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (seatImg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    int imgW = seatImg.getWidth(null);
                    int imgH = seatImg.getHeight(null);
                    if (imgW > 0 && imgH > 0) {
                        double scale = Math.min(18.0 / imgW, 18.0 / imgH);
                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);
                        int drawX = (18 - drawW) / 2;
                        int drawY = (18 - drawH) / 2;
                        g2.drawImage(seatImg, drawX, drawY, drawW, drawH, this);
                    } else {
                        g2.drawImage(seatImg, 0, 0, 18, 18, this);
                    }
                    g2.dispose();
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect(0, 0, 18, 18);
                }
            }
        };
        icon.setPreferredSize(new Dimension(18, 18));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(80, 85, 100));

        item.add(icon);
        item.add(lbl);
        return item;
    }

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
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Status row
        JPanel statusBox = new JPanel();
        statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.Y_AXIS));
        statusBox.setOpaque(false);
        statusBox.setAlignmentX(LEFT_ALIGNMENT);
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        statusBox.add(sectionLabel("Trạng thái"));
        statusBox.add(Box.createRigidArea(new Dimension(0, 5)));

        CinemaHallStatus curStatus = (currentHall != null && currentHall.getStatus() != null)
                ? currentHall.getStatus()
                : CinemaHallStatus.ACTIVE;
        cmbStatus = new JComboBox<>(new String[] { "Đang hoạt động", "Không hoạt động" });
        cmbStatus.setFont(FONT_SUB);
        cmbStatus.setSelectedItem(curStatus == CinemaHallStatus.ACTIVE ? "Đang hoạt động" : "Không hoạt động");
        cmbStatus.setBorder(new LineBorder(new Color(210, 215, 225), 1, true));
        cmbStatus.setAlignmentX(LEFT_ALIGNMENT);
        // Colour-code the selected item via renderer
        cmbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    lbl.setForeground("Đang hoạt động".equals(value)
                            ? new Color(22, 163, 74) // green
                            : new Color(185, 28, 28)); // red
                }
                return lbl;
            }
        });
        statusBox.add(cmbStatus);
        card.add(statusBox);
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
        String statusStr = cmbStatus.getSelectedItem().toString();
        CinemaHallStatus newStatus = statusStr.equals("Đang hoạt động") ? CinemaHallStatus.ACTIVE
                : CinemaHallStatus.INACTIVE;

        boolean nameOrTypeChanged = !newName.equals(currentHall.getName())
                || !newType.equals(currentHall.getHallType());
        boolean statusChanged = newStatus != null && newStatus != currentHall.getStatus();

        if (nameOrTypeChanged || statusChanged) {

            if (!newName.equalsIgnoreCase(currentHall.getName())) {
                boolean isDuplicate = allHalls.stream().anyMatch(
                        h -> h.getName().equalsIgnoreCase(newName) && !h.getHallId().equals(currentHall.getHallId()));
                if (isDuplicate) {
                    showModernMessageDialog("Trùng lập", "Tên phòng chiếu đã tồn tại, vui lòng chọn tên khác!", true);
                    return;
                }
            }

            hasHallChange = true;
            try {
                if (nameOrTypeChanged) {
                    hallDao.updateHallInfo(currentHall.getHallId(), newName, newType);
                    currentHall.setName(newName);
                    currentHall.setHallType(newType);
                }
                if (statusChanged) {
                    hallDao.updateHallStatus(currentHall.getHallId(), newStatus);
                    currentHall.setStatus(newStatus);
                    // Refresh seat interactivity
                    refreshSeatInteractivity();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showModernMessageDialog("Lỗi", "Lỗi khi cập nhật thông tin rạp:\n" + e.getMessage(), true);
                return;
            }
        }

        if (hasSeatChange) {
            // Chỉ cho phép lưu ghế khi phòng đang ACTIVE
            if (currentHall.getStatus() != CinemaHallStatus.ACTIVE) {
                showModernMessageDialog("Không được phép", "Không thể lưu trạng thái ghế khi phòng đang INACTIVE!",
                        true);
                return;
            }
            try {
                hallDao.updateSeatStatuses(modifiedSeats);
                modifiedSeats.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                showModernMessageDialog("Lỗi", "Lỗi khi lưu ghế:\n" + e.getMessage(), true);
                return;
            }
        }

        if (hasHallChange || hasSeatChange) {
            showModernMessageDialog("Thành công", "Đã lưu thay đổi thành công!", false);
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
            showModernMessageDialog("Thông báo", "Không có thay đổi nào cần lưu.", false);
        }
    }

    // Click khi sửa thông tin ghế
    private void refreshSeatInteractivity() {
        if (seatContainer == null)
            return;
        boolean isActive = currentHall != null && currentHall.getStatus() == CinemaHallStatus.ACTIVE;
        enableComponents(seatContainer, isActive);
    }

    private void enableComponents(Container container, boolean enable) {
        for (Component c : container.getComponents()) {
            c.setEnabled(enable);
            if (c instanceof Container)
                enableComponents((Container) c, enable);
        }
    }

    private JPanel buildPricingCard() {
        double currentStd = 1.0;
        double currentVip = 1.5;
        if (spnStdFactor != null) {
            currentStd = ((Number) spnStdFactor.getValue()).doubleValue();
        } else if (!seatMap.isEmpty()) {
            for (Seat s : seatMap.values()) {
                if (s.getSeatType() == SeatType.REGULAR && s.getSeatFactor() > 0)
                    currentStd = s.getSeatFactor();
                else if (s.getSeatType() == SeatType.VIP && s.getSeatFactor() > 0)
                    currentVip = s.getSeatFactor();
            }
        }
        if (spnVipFactor != null) {
            currentVip = ((Number) spnVipFactor.getValue()).doubleValue();
        }

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 235), 1, true),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Hệ số giá ghế mặc định");
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
        SpinnerNumberModel stdModel = new SpinnerNumberModel(currentStd, 0.0, 10.0, 0.1);
        spnStdFactor = new JSpinner(stdModel);
        spnStdFactor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spnStdFactor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnStdFactor.setAlignmentX(LEFT_ALIGNMENT);
        JSpinner.NumberEditor stdEditor = new JSpinner.NumberEditor(spnStdFactor, "0.0");
        spnStdFactor.setEditor(stdEditor);

        SpinnerNumberModel vipModel = new SpinnerNumberModel(currentVip, 0.0, 10.0, 0.1);
        spnVipFactor = new JSpinner(vipModel);
        spnVipFactor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spnVipFactor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnVipFactor.setAlignmentX(LEFT_ALIGNMENT);
        JSpinner.NumberEditor vipEditor = new JSpinner.NumberEditor(spnVipFactor, "0.0");
        spnVipFactor.setEditor(vipEditor);

        card.add(pricingRow("Phổ thông", false, spnStdFactor));
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(pricingRow("Vip", true, spnVipFactor));
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

        try {
            if (spnStdFactor != null)
                spnStdFactor.commitEdit();
            if (spnVipFactor != null)
                spnVipFactor.commitEdit();
        } catch (java.text.ParseException pe) {
            showModernMessageDialog("Lỗi nhập liệu", "Vui lòng nhập hệ số hợp lệ!", true);
            return;
        }

        double stdFactor = ((Number) spnStdFactor.getValue()).doubleValue();
        double vipFactor = ((Number) spnVipFactor.getValue()).doubleValue();
        try {
            hallDao.updateSeatFactorByType(currentHall.getHallId(), SeatType.REGULAR, stdFactor);
            hallDao.updateSeatFactorByType(currentHall.getHallId(), SeatType.VIP, vipFactor);
            showModernMessageDialog("Thành công",
                    String.format("Đã cập nhật hệ số thành công!%n  Phổ thông: %.1f x%n  Vip:      %.1f x",
                            stdFactor, vipFactor),
                    false);
            // Refresh loaded data so next reload reflects the correct prices
            loadSeatsForHall(currentHall.getHallId());
        } catch (SQLException ex) {
            ex.printStackTrace();
            showModernMessageDialog("Lỗi", "Lỗi khi cập nhật hệ số:\n" + ex.getMessage(), true);
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

        JPanel spinWrap = new JPanel(new BorderLayout(5, 0));
        spinWrap.setOpaque(false);
        spinWrap.add(spinner, BorderLayout.CENTER);

        JLabel lblVND = new JLabel("x");
        lblVND.setFont(FONT_LABEL);
        lblVND.setForeground(new Color(130, 135, 155));
        spinWrap.add(lblVND, BorderLayout.EAST);

        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(spinWrap, BorderLayout.CENTER);
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
        if (cmbStatus != null) {
            CinemaHallStatus s = currentHall.getStatus();
            cmbStatus.setSelectedItem(s == CinemaHallStatus.ACTIVE ? "Đang hoạt động" : "Không hoạt động");
        }

        // Rebuild grid ghế theo đúng số hàng/cột của phòng mới
        if (seatContainer != null) {
            seatContainer.removeAll();
            seatContainer.add(buildSeatGrid(), BorderLayout.CENTER);
            seatContainer.revalidate();
            seatContainer.repaint();
        }

        refreshSeatInteractivity();
    }

    private void showModernMessageDialog(String title, String message, boolean isError) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setUndecorated(true);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_PAGE);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(WHITE);
        Color borderColor = isError ? ACCENT_RED : new Color(22, 163, 74);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, borderColor),
                new EmptyBorder(25, 30, 25, 30)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(borderColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel messageLabel = new JLabel("<html><body style='width: 300px; word-wrap: break-word;'>"
                + message.replace("\n", "<br>") + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(100, 110, 120));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color btnBg = isError ? ACCENT_RED : new Color(22, 163, 74);
        Color btnHover = isError ? new Color(185, 28, 28) : new Color(21, 128, 61);
        JButton okBtn = createRoundedButton("OK", btnBg, WHITE, btnHover);
        okBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(okBtn);

        mainPanel.add(btnPanel);
        wrapper.add(mainPanel, BorderLayout.CENTER);

        dialog.setContentPane(wrapper);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private JButton createRoundedButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 36));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
                btn.repaint();
            }
        });
        return btn;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
    }
}
