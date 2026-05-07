package com.rapphim.view.dialogs;

import com.rapphim.service.ShowtimeService;
import com.rapphim.service.MovieService;
import com.rapphim.service.HallService;
import com.rapphim.model.CinemaHall;
import com.rapphim.model.Movie;
import com.rapphim.model.Showtime;
import com.rapphim.model.enums.CinemaHallStatus;
import com.rapphim.model.enums.MovieStatus;
import com.rapphim.model.enums.ShowtimeStatus;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class AddShowTimeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_SECONDARY = new Color(130, 135, 148);
    private static final Color ACCENT = new Color(220, 38, 38);

    private static final Color BORDER_COLOR = new Color(225, 228, 235);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color INFO = new Color(59, 130, 246);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private final ShowtimeService showtimeService = new ShowtimeService();
    private final MovieService movieService = new MovieService();
    private final HallService hallService = new HallService();

    private List<Movie> activeMovies;
    private List<CinemaHall> activeHalls;
    private boolean saved = false;

    private JTextField txtId;
    private JComboBox<String> cmbMovie, cmbHall;
    private JSpinner spnDay, spnMonth, spnYear, spnHour, spnMin, spnPrice;
    private JLabel lblSystemError;

    public AddShowTimeDialog(JFrame parent) {
        super(parent, "Lịch Chiếu", true);
        loadData();
        initUI();
    }

    public boolean isSaved() {
        return saved;
    }

    private void loadData() {
        try {
            activeMovies = movieService.getAllMovies().stream()
                    .filter(m -> m.getStatus() == MovieStatus.ACTIVE).collect(Collectors.toList());
        } catch (SQLException e) {
            activeMovies = List.of();
        }
        try {
            activeHalls = hallService.getAllHalls().stream()
                    .filter(h -> h.getStatus() == CinemaHallStatus.ACTIVE).collect(Collectors.toList());
        } catch (SQLException e) {
            activeHalls = List.of();
        }
    }

    private void initUI() {
        setSize(520, 680);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setUndecorated(true);
        getContentPane().setBackground(BG_COLOR);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(SURFACE);
        main.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_COLOR),
                new EmptyBorder(28, 32, 28, 32)));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel lbTitle = new JLabel("Lịch Chiếu");
        lbTitle.setFont(FONT_TITLE);
        lbTitle.setForeground(TEXT_PRIMARY);

        JLabel lbSub = new JLabel("Tạo phim vào phòng và khung giờ chiếu.");
        lbSub.setFont(FONT_BODY);
        lbSub.setForeground(TEXT_SECONDARY);

        titleBox.add(lbTitle);
        titleBox.add(lbSub);
        header.add(titleBox, BorderLayout.WEST);
        header.add(createCloseButton(), BorderLayout.EAST);
        main.add(header);
        main.add(Box.createRigidArea(new Dimension(0, 8)));

        // Separator
        JPanel sep = new JPanel();
        sep.setBackground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        main.add(sep);
        main.add(Box.createRigidArea(new Dimension(0, 16)));

        // Showtime ID (auto)
        txtId = styledField("Mã suất chiếu (Tự động)");
        txtId.setEditable(false);
        txtId.setBackground(new Color(238, 242, 255));
        txtId.setForeground(INFO);
        txtId.setFont(FONT_BOLD);
        try {
            txtId.setText(showtimeService.getNextShowtimeId());
        } catch (SQLException e) {
            txtId.setText("");
        }
        main.add(fieldRow("MÃ SUẤT CHIẾU", txtId, null));

        // Movie combo
        cmbMovie = new JComboBox<>();
        cmbMovie.addItem("Chọn phim...");
        for (Movie m : activeMovies)
            cmbMovie.addItem(m.getMovieId() + " - " + m.getTitle());
        cmbMovie.setFont(FONT_BODY.deriveFont(14f));
        cmbMovie.setBackground(SURFACE);
        cmbMovie.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        main.add(fieldRow("CHỌN PHIM", cmbMovie, null));

        // Hall + Start Time (two columns)
        cmbHall = new JComboBox<>();
        cmbHall.addItem("Chọn phòng chiếu...");
        for (CinemaHall ch : activeHalls)
            cmbHall.addItem(ch.getHallId() + " - " + ch.getName());
        cmbHall.setFont(FONT_BODY.deriveFont(14f));
        cmbHall.setBackground(SURFACE);
        cmbHall.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        LocalDateTime now = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0);
        JPanel dtPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        dtPanel.setOpaque(false);
        spnDay = miniSpn(now.getDayOfMonth(), 1, 31);
        spnMonth = miniSpn(now.getMonthValue(), 1, 12);
        spnYear = miniSpn(now.getYear(), 2024, 2030);
        spnHour = miniSpn(now.getHour(), 0, 23);
        spnMin = miniSpn(0, 0, 59);
        dtPanel.add(spnDay);
        dtPanel.add(lbl("/"));
        dtPanel.add(spnMonth);
        dtPanel.add(lbl("/"));
        dtPanel.add(spnYear);
        dtPanel.add(Box.createRigidArea(new Dimension(6, 0)));
        dtPanel.add(spnHour);
        dtPanel.add(lbl(":"));
        dtPanel.add(spnMin);
        main.add(fieldRow("PHÒNG CHIẾU", cmbHall, null));
        main.add(fieldRow("THỜI GIAN BẮT ĐẦU", dtPanel, null));

        // Base Price
        spnPrice = new JSpinner(new SpinnerNumberModel(120000, 0, 10000000, 10000));
        spnPrice.setFont(FONT_BODY.deriveFont(14f));
        JComponent ed = spnPrice.getEditor();
        if (ed instanceof JSpinner.NumberEditor)
            ((JSpinner.NumberEditor) ed).getFormat().setGroupingUsed(true);
        main.add(fieldRow("GIÁ VÉ CƠ BẢN (VNĐ)", spnPrice, null));

        // Error
        lblSystemError = new JLabel(" ");
        lblSystemError.setFont(FONT_SMALL.deriveFont(Font.ITALIC));
        lblSystemError.setForeground(ACCENT);
        lblSystemError.setAlignmentX(LEFT_ALIGNMENT);
        main.add(lblSystemError);
        main.add(Box.createRigidArea(new Dimension(0, 8)));

        // Buttons
        main.add(buttonRow());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(main, BorderLayout.CENTER);
        setContentPane(wrapper);
    }

    // ── LOGIC ────────────────────────────────────────────────────────────────

    private void handleCreate() {
        lblSystemError.setText(" ");
        Movie mv = getSelectedMovie();
        if (mv == null) {
            lblSystemError.setText("Vui lòng chọn phim.");
            return;
        }
        CinemaHall hall = getSelectedHall();
        if (hall == null) {
            lblSystemError.setText("Vui lòng chọn phòng chiếu.");
            return;
        }
        LocalDateTime start = getStartTime();
        if (start == null || start.isBefore(LocalDateTime.now())) {
            lblSystemError.setText("Thời gian bắt đầu phải sau thời gian hiện tại.");
            return;
        }
        LocalDateTime end = start.plusMinutes(mv.getDurationMins() + 15);
        double price = ((Number) spnPrice.getValue()).doubleValue();
        if (price <= 0) {
            lblSystemError.setText("Giá phải lớn hơn 0.");
            return;
        }
        try {
            if (showtimeService.hasOverlap(hall.getHallId(), start, end)) {
                lblSystemError
                        .setText("Đã trùng lịch! Phòng " + hall.getName() + " đã có suất chiếu trong khung giờ này.");
                return;
            }
        } catch (SQLException ex) {
            lblSystemError.setText("Lỗi kiểm tra: " + ex.getMessage());
            return;
        }

        Showtime st2 = new Showtime();
        st2.setShowtimeId(txtId.getText());
        st2.setMovieId(mv.getMovieId());
        st2.setHallId(hall.getHallId());
        st2.setStartTime(start);
        st2.setEndTime(end);
        st2.setBasePrice(price);
        st2.setStatus(ShowtimeStatus.SCHEDULED);
        try {
            showtimeService.addShowtime(st2);
            showtimeService.generateShowSeats(st2.getShowtimeId(), hall.getHallId(), price);
            saved = true;
            showSuccessDialog(st2.getShowtimeId(), mv.getTitle());
            dispose();
        } catch (SQLException ex) {
            lblSystemError.setText("Lỗi tạo suất chiếu: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Movie getSelectedMovie() {
        int i = cmbMovie.getSelectedIndex();
        return i > 0 ? activeMovies.get(i - 1) : null;
    }

    private CinemaHall getSelectedHall() {
        int i = cmbHall.getSelectedIndex();
        return i > 0 ? activeHalls.get(i - 1) : null;
    }

    private LocalDateTime getStartTime() {
        try {
            return LocalDateTime.of((int) spnYear.getValue(), (int) spnMonth.getValue(),
                    (int) spnDay.getValue(), (int) spnHour.getValue(), (int) spnMin.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    // ── UI HELPERS ───────────────────────────────────────────────────────────

    private JPanel fieldRow(String label, Component input, JLabel warn) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, warn != null ? 88 : 72));
        JLabel lb = new JLabel(label);
        lb.setFont(FONT_BOLD.deriveFont(12f));
        lb.setForeground(TEXT_SECONDARY);
        lb.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lb);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        if (input instanceof JTextField)
            ((JTextField) input).setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        ((JComponent) input).setAlignmentX(LEFT_ALIGNMENT);
        p.add(input);
        if (warn != null) {
            warn.setAlignmentX(LEFT_ALIGNMENT);
            p.add(warn);
        }
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        return p;
    }

    private JPanel buttonRow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        Color btnBg = new Color(243, 244, 246);
        Color btnHover = new Color(229, 231, 235);
        JButton cancelBtn = roundedBtn("HỦY", btnBg, TEXT_PRIMARY, btnHover);
        cancelBtn.addActionListener(e -> dispose());
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        panel.add(cancelBtn, gbc);

        JButton saveBtn = roundedBtn("LƯU LỊCH CHIẾU", TEXT_PRIMARY, SURFACE, new Color(50, 50, 65));
        saveBtn.addActionListener(e -> handleCreate());
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(saveBtn, gbc);
        return panel;
    }

    private JTextField styledField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(FONT_BODY.deriveFont(14f));
        field.setForeground(TEXT_SECONDARY);
        field.setText(placeholder);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR), new EmptyBorder(8, 14, 8, 14)));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(SURFACE);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_SECONDARY);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private JSpinner miniSpn(int val, int min, int max) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
        sp.setFont(FONT_BODY.deriveFont(14f));
        sp.setPreferredSize(new Dimension(55, 34));
        JComponent ed = sp.getEditor();
        if (ed instanceof JSpinner.NumberEditor)
            ((JSpinner.NumberEditor) ed).getFormat().setGroupingUsed(false);
        return sp;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(FONT_BODY.deriveFont(14f));
        return l;
    }

    private JButton createCloseButton() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int pad = 7, w = getWidth(), h = getHeight();
                g2.drawLine(pad, pad, w - pad, h - pad);
                g2.drawLine(w - pad, pad, pad, h - pad);
                g2.dispose();
            }
        };
        btn.setForeground(TEXT_SECONDARY);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> dispose());
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(ACCENT);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_SECONDARY);
                btn.repaint();
            }
        });
        return btn;
    }

    private JButton roundedBtn(String text, Color bg, Color fg, Color hover) {
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
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 44));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
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

    private void showSuccessDialog(String id, String movieTitle) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "", true);
        dlg.setUndecorated(true);
        dlg.setSize(360, 200);
        dlg.setLocationRelativeTo(parent);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_COLOR), new EmptyBorder(24, 24, 24, 24)));
        JLabel ico = new JLabel("\u2714");
        ico.setFont(new Font("Segoe UI", Font.BOLD, 28));
        ico.setForeground(SUCCESS);
        ico.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(ico);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        JLabel msg = new JLabel("<html><center>Suất chiếu <b>" + id + "</b><br>\"" + movieTitle
                + "\"<br>Đã tạo thành công</center></html>");
        msg.setFont(FONT_BODY);
        msg.setForeground(TEXT_PRIMARY);
        msg.setAlignmentX(CENTER_ALIGNMENT);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(msg);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        JButton ok = roundedBtn("OK", SUCCESS, SURFACE, new Color(16, 130, 55));
        ok.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        ok.setAlignmentX(CENTER_ALIGNMENT);
        ok.addActionListener(e -> dlg.dispose());
        panel.add(ok);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_COLOR);
        wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        wrap.add(panel);
        dlg.setContentPane(wrap);
        dlg.setVisible(true);
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int r, Color c) {
            this.radius = r;
            this.color = c;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets i) {
            i.set(1, 1, 1, 1);
            return i;
        }
    }
}
