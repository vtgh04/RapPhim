package com.rapphim.view.panels;

import com.rapphim.view.dialogs.AddShowTimeDialog;
import com.rapphim.dao.MovieDAO;
import com.rapphim.dao.ShowtimeDAO;
import com.rapphim.model.Movie;
import com.rapphim.model.Showtime;
import com.rapphim.model.enums.ShowtimeStatus;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class ShowtimesPanel extends JPanel {

    private static final Color BG = new Color(240, 242, 245);
    private static final Color TXT = new Color(25, 25, 35);
    private static final Color MUTED = new Color(120, 125, 140);
    private static final Color BORDER = new Color(225, 228, 235);
    private static final Color RED = new Color(220, 38, 38);

    private static final Font F_NORM = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_MONO = new Font("Consolas", Font.BOLD, 18);

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ShowtimeDAO stDao = new ShowtimeDAO();
    private final MovieDAO mvDao = new MovieDAO();
    private Map<String, Movie> movieCache = new HashMap<>();
    private List<Showtime> todayList = new ArrayList<>();
    private Showtime selected;
    private LocalDate selectedDate = LocalDate.now();
    private JPanel statsRow;
    private CardLayout centerCardLayout;
    private JPanel centerCardPanel;
    private JPanel listPanel, detailPanel;
    private JTextField searchField, priceField;

    public ShowtimesPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        loadData();

        add(buildNorth(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        // F5 refresh
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "refresh");
        getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
                updateStatsUI();
                selected = null;
                populateList("");
                showEmpty();
            }
        });
    }

    private void loadData() {
        try {
            stDao.autoUpdateStatuses(LocalDateTime.now());
            LocalDateTime from = selectedDate.atTime(0, 0);
            LocalDateTime to = from.plusDays(1);
            todayList = stDao.findByDateRange(from, to);
            List<Movie> movies = mvDao.findAll();
            movieCache.clear();
            for (Movie m : movies)
                movieCache.put(m.getMovieId(), m);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Movie getMovie(String movieId) {
        return movieCache.getOrDefault(movieId, null);
    }

    // ════════════ NORTH ════════════
    private JPanel buildNorth() {
        JPanel n = new JPanel(new BorderLayout(0, 14));
        n.setOpaque(false);
        n.setBorder(new EmptyBorder(0, 0, 18, 0));

        // Title row
        JPanel tr = new JPanel(new BorderLayout());
        tr.setOpaque(false);
        JPanel tb = new JPanel();
        tb.setLayout(new BoxLayout(tb, BoxLayout.Y_AXIS));
        tb.setOpaque(false);
        JLabel t1 = new JLabel("Lịch Chiếu Phim");
        t1.setFont(F_BOLD.deriveFont(22f));
        t1.setForeground(TXT);
        JLabel t2 = new JLabel("Quản lý suất chiếu theo ngày");
        t2.setFont(F_NORM);
        t2.setForeground(MUTED);
        tb.add(t1);
        tb.add(t2);
        tr.add(tb, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton btnExport = mkBtn("Export Excel", new Color(243, 244, 246), new Color(229, 231, 235));
        btnExport.setForeground(TXT);
        btnExport.addActionListener(e -> handleExport());
        btnRow.add(btnExport);

        JButton btnImport = mkBtn("Import Excel", new Color(243, 244, 246), new Color(229, 231, 235));
        btnImport.setForeground(TXT);
        btnImport.addActionListener(e -> handleImport());
        btnRow.add(btnImport);

        JButton btnAdd = mkBtn("Tạo suất chiếu", RED, new Color(185, 28, 28));
        btnAdd.setPreferredSize(new Dimension(180, 38));
        btnAdd.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ShowtimesPanel.this);
            AddShowTimeDialog dlg = new AddShowTimeDialog(parentFrame);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                loadData();
                updateStatsUI();
                selected = null;
                populateList("");
                showEmpty();
            }
        });
        btnRow.add(btnAdd);

        tr.add(btnRow, BorderLayout.EAST);
        n.add(tr, BorderLayout.NORTH);

        // Stats row
        statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setOpaque(false);
        updateStatsUI();
        n.add(statsRow, BorderLayout.CENTER);

        // Search and Date row
        JPanel sRow = new JPanel(new BorderLayout(14, 0));
        sRow.setOpaque(false);
        sRow.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setOpaque(false);
        searchField = new JTextField();
        searchField.setFont(F_NORM);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                populateList(searchField.getText().trim());
            }
        });
        searchBox.add(new JLabel("🔍"), BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        JDateChooser dateChooser = new JDateChooser(java.sql.Date.valueOf(selectedDate));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(F_NORM);
        dateChooser.setPreferredSize(new Dimension(160, 36));
        dateChooser.addPropertyChangeListener("date", e -> {
            if (e.getNewValue() != null) {
                java.util.Date d = (java.util.Date) e.getNewValue();
                selectedDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                loadData();
                updateStatsUI();
                selected = null;
                populateList(searchField.getText().trim());
                showEmpty();
            }
        });

        sRow.add(searchBox, BorderLayout.CENTER);
        sRow.add(dateChooser, BorderLayout.EAST);
        n.add(sRow, BorderLayout.SOUTH);

        return n;
    }

    private void updateStatsUI() {
        statsRow.removeAll();
        int total = todayList.size();
        int sched = 0;
        Set<String> activeHalls = new HashSet<>();
        for (Showtime st : todayList) {
            if (st.getStatus() == ShowtimeStatus.SCHEDULED) {
                sched++;
            }
            if (st.getStatus() == ShowtimeStatus.SCHEDULED || st.getStatus() == ShowtimeStatus.ONGOING) {
                activeHalls.add(st.getHallId());
            }
        }
        int halls = activeHalls.size();
        statsRow.add(statCard("Tổng suất chiếu", total, new Color(59, 130, 246)));
        statsRow.add(statCard("Đã lên lịch", sched, new Color(245, 158, 11)));
        statsRow.add(statCard("Phòng hoạt động", halls, new Color(16, 185, 129)));
        statsRow.revalidate();
        statsRow.repaint();
    }

    private JPanel statCard(String label, int val, Color accent) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setBackground(Color.WHITE);
        c.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(16, 20, 16, 20)));
        JLabel v = new JLabel(String.valueOf(val));
        v.setFont(F_BOLD.deriveFont(32f));
        v.setForeground(accent);
        JLabel l = new JLabel(label);
        l.setFont(F_NORM);
        l.setForeground(MUTED);
        c.add(v, BorderLayout.CENTER);
        c.add(l, BorderLayout.SOUTH);
        return c;
    }

    // ════════════ CENTER ════════════
    private JPanel buildCenter() {
        centerCardLayout = new CardLayout();
        centerCardPanel = new JPanel(centerCardLayout);
        centerCardPanel.setOpaque(false);

        JPanel ctr = new JPanel(new BorderLayout(12, 0));
        ctr.setOpaque(false);

        // pLeft scrolls independently
        ctr.add(buildLeft(), BorderLayout.CENTER);

        // pRight (detailPanel) is fixed/sticky – not inside any scroll
        JPanel rightWrap = buildRight();
        rightWrap.setPreferredSize(new Dimension(320, 0));
        ctr.add(rightWrap, BorderLayout.EAST);

        centerCardPanel.add(ctr, "LIST_VIEW");
        return centerCardPanel;
    }

    // ════════════ LEFT ════════════
    private JPanel buildLeft() {
        JPanel w = new JPanel(new BorderLayout(0, 10));
        w.setOpaque(false);
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG);
        populateList("");
        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG);
        w.add(sp, BorderLayout.CENTER);
        return w;
    }

    private void populateList(String filter) {
        listPanel.removeAll();
        // Group by movieId
        Map<String, List<Showtime>> grouped = new LinkedHashMap<>();
        for (Showtime st : todayList) {
            grouped.computeIfAbsent(st.getMovieId(), k -> new ArrayList<>()).add(st);
        }
        String lf = filter.toLowerCase();
        for (Map.Entry<String, List<Showtime>> entry : grouped.entrySet()) {
            Movie mv = getMovie(entry.getKey());
            String title = mv != null ? mv.getTitle() : entry.getKey();
            if (!lf.isEmpty() && !title.toLowerCase().contains(lf)
                    && !entry.getKey().toLowerCase().contains(lf))
                continue;
            listPanel.add(buildMovieRow(mv, entry.getValue()));
            listPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }
        if (listPanel.getComponentCount() == 0) {
            JLabel e = new JLabel("Không tìm thấy suất chiếu.");
            e.setFont(F_NORM);
            e.setForeground(MUTED);
            e.setBorder(new EmptyBorder(20, 12, 20, 12));
            listPanel.add(e);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildMovieRow(Movie mv, List<Showtime> shows) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(14, 14, 14, 14)));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Left: poster + title
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(120, 160));

        JLabel poster = new JLabel();
        poster.setPreferredSize(new Dimension(100, 140));
        poster.setMaximumSize(new Dimension(100, 140));
        poster.setAlignmentX(CENTER_ALIGNMENT);
        poster.setHorizontalAlignment(SwingConstants.CENTER);
        poster.setBackground(new Color(230, 232, 240));
        poster.setOpaque(true);
        poster.setBorder(new LineBorder(BORDER, 1, true));
        if (mv != null && mv.getPosterUrl() != null && !mv.getPosterUrl().isEmpty()) {
            try {
                ImageIcon ico = new ImageIcon(getClass().getClassLoader().getResource(mv.getPosterUrl()));
                Image img = ico.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
                poster.setIcon(new ImageIcon(img));
                poster.setText("");
            } catch (Exception ex) {
                poster.setText(mv.getMovieId());
                poster.setFont(F_NORM.deriveFont(11f));
                poster.setForeground(MUTED);
            }
        } else {
            poster.setText(mv != null ? mv.getMovieId() : "?");
            poster.setFont(F_NORM.deriveFont(11f));
            poster.setForeground(MUTED);
        }
        left.add(poster);
        left.add(Box.createRigidArea(new Dimension(0, 6)));
        JLabel titleLbl = new JLabel(mv != null ? mv.getTitle() : "N/A");
        titleLbl.setFont(F_BOLD);
        titleLbl.setForeground(TXT);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        left.add(titleLbl);
        row.add(left, BorderLayout.WEST);

        // Right: showtime cards grid (2 columns)
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setOpaque(false);
        for (Showtime st : shows) {
            grid.add(buildSlotCard(st));
        }
        if (shows.size() % 2 != 0) {
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            grid.add(filler);
        }
        JPanel gridWrap = new JPanel(new BorderLayout());
        gridWrap.setOpaque(false);
        gridWrap.add(grid, BorderLayout.NORTH);
        row.add(gridWrap, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildSlotCard(Showtime st) {
        boolean sel = st.equals(selected);
        Color bgCard = sel ? new Color(255, 237, 237) : new Color(248, 249, 252);
        Color bdr = sel ? RED : BORDER;

        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(bgCard);
        card.setBorder(new CompoundBorder(new LineBorder(bdr, sel ? 2 : 1, true), new EmptyBorder(10, 12, 10, 12)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top: badge + ID
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(badge(st.getStatus()), BorderLayout.WEST);
        JLabel id = new JLabel("#" + st.getShowtimeId());
        id.setFont(F_NORM.deriveFont(11f));
        id.setForeground(MUTED);
        top.add(id, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Time
        JLabel time = new JLabel(st.getStartTime().format(TF));
        time.setFont(F_BOLD.deriveFont(20f));
        time.setForeground(TXT);
        card.add(time, BorderLayout.CENTER);

        // Bottom: hall + price
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);
        JLabel hall = new JLabel("HALL " + st.getHallId().replaceAll("[^0-9]", ""));
        hall.setFont(F_NORM.deriveFont(11f));
        hall.setForeground(MUTED);
        JLabel price = new JLabel(((long) st.getBasePrice() / 1000) + "k");
        price.setFont(F_BOLD);
        price.setForeground(TXT);
        bot.add(hall, BorderLayout.WEST);
        bot.add(price, BorderLayout.EAST);
        card.add(bot, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectST(st);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!st.equals(selected)) {
                    card.setBackground(new Color(243, 244, 250));
                    card.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!st.equals(selected)) {
                    card.setBackground(bgCard);
                    card.repaint();
                }
            }
        });
        return card;
    }

    // ════════════ RIGHT ════════════
    private JPanel buildRight() {
        detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(12, 14, 12, 14)));
        showEmpty();
        return detailPanel;
    }

    private void showEmpty() {
        detailPanel.removeAll();
        JLabel h = new JLabel("Thông tin suất chiếu");
        h.setFont(F_BOLD);
        h.setForeground(TXT);
        h.setAlignmentX(LEFT_ALIGNMENT);
        detailPanel.add(h);
        detailPanel.add(Box.createVerticalGlue());
        JLabel msg = new JLabel("Vui lòng chọn suất chiếu để xem thông tin chi tiết");
        msg.setFont(F_NORM);
        msg.setForeground(MUTED);
        msg.setAlignmentX(CENTER_ALIGNMENT);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        detailPanel.add(msg);
        detailPanel.add(Box.createVerticalGlue());
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private void showDetail(Showtime st) {
        detailPanel.removeAll();
        Movie mv = getMovie(st.getMovieId());

        // Header
        JLabel h = new JLabel("Thông tin suất chiếu");
        h.setFont(F_BOLD);
        h.setForeground(TXT);
        h.setAlignmentX(LEFT_ALIGNMENT);
        detailPanel.add(h);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Info grid
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(new Color(248, 249, 252));
        info.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 2, 12)));
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        infoRow(info, "Movie:", mv != null ? mv.getTitle() : st.getMovieId());
        infoRow(info, "Hall:", "HALL " + st.getHallId().replaceAll("[^0-9]", ""));
        infoRow(info, "Start:", st.getStartTime().format(TF));
        infoRow(info, "End:", st.getEndTime().format(TF));
        infoRow(info, "Date:", st.getStartTime().format(DF));
        infoRow(info, "Status:", st.getStatus().getValue());

        detailPanel.add(info);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Price editor
        JLabel pl = new JLabel("Base Price (VND)");
        pl.setFont(F_BOLD);
        pl.setForeground(TXT);
        pl.setAlignmentX(LEFT_ALIGNMENT);
        detailPanel.add(pl);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        priceField = new JTextField(NumberFormat.getNumberInstance().format((long) st.getBasePrice()));
        priceField.setFont(F_MONO);
        priceField.setHorizontalAlignment(SwingConstants.RIGHT);
        priceField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        priceField.setAlignmentX(LEFT_ALIGNMENT);
        detailPanel.add(priceField);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Update button
        JButton btn = mkBtn("Cập nhật thông tin", RED, new Color(185, 28, 28));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> handleUpdate(st));
        detailPanel.add(btn);

        // Xem ghế button
        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSeatMap = mkBtn("Xem sơ đồ ghế", new Color(234, 179, 8), new Color(202, 138, 4));
        btnSeatMap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSeatMap.setAlignmentX(LEFT_ALIGNMENT);
        btnSeatMap.addActionListener(e -> showSeatMap(st));
        detailPanel.add(btnSeatMap);

        if (st.getStatus() == ShowtimeStatus.SCHEDULED) {
            detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            JButton btnCancel = mkBtn("Hủy suất chiếu", new Color(156, 163, 175), new Color(107, 114, 128));
            btnCancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btnCancel.setAlignmentX(LEFT_ALIGNMENT);
            btnCancel.addActionListener(e -> handleCancel(st));
            detailPanel.add(btnCancel);
        }

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private void infoRow(JPanel p, String k, String v) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel kl = new JLabel(k);
        kl.setFont(F_NORM.deriveFont(11f));
        kl.setForeground(MUTED);
        kl.setVerticalAlignment(SwingConstants.TOP);
        kl.setPreferredSize(new Dimension(65, 20));

        // Use JTextArea for dynamic word wrapping to the exact container edge
        JTextArea vl = new JTextArea(v);
        vl.setFont(F_BOLD);
        vl.setForeground(TXT);
        vl.setLineWrap(true);
        vl.setWrapStyleWord(true);
        vl.setOpaque(false);
        vl.setEditable(false);
        vl.setFocusable(false);
        vl.setBorder(BorderFactory.createEmptyBorder());

        row.add(kl, BorderLayout.WEST);
        row.add(vl, BorderLayout.CENTER);

        p.add(row);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showSeatMap(Showtime st) {
        JPanel seatView = new JPanel(new BorderLayout());
        seatView.setBackground(Color.WHITE);
        seatView.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(20, 20, 20, 20)));

        // Top header with Back Button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton btnBack = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/icons/left-arrow.png"));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            btnBack.setIcon(new ImageIcon(img));
        } catch (Exception e) {
        }
        btnBack.setToolTipText("Quay về");
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> centerCardLayout.show(centerCardPanel, "LIST_VIEW"));
        header.add(btnBack, BorderLayout.WEST);

        JLabel title = new JLabel("Sơ đồ ghế - " + st.getShowtimeId() + " (" + st.getHallId() + ")",
                SwingConstants.CENTER);
        title.setFont(F_BOLD.deriveFont(18f));
        title.setForeground(TXT);
        header.add(title, BorderLayout.CENTER);

        seatView.add(header, BorderLayout.NORTH);

        // Fetch data
        com.rapphim.dao.HallDao hallDao = new com.rapphim.dao.HallDao();
        com.rapphim.model.CinemaHall hall = null;
        java.util.List<com.rapphim.model.Seat> seats = null;
        java.util.Map<String, com.rapphim.model.enums.ShowSeatStatus> statuses = null;
        try {
            hall = hallDao.findHallById(st.getHallId());
            seats = hallDao.findSeatsByHall(st.getHallId());
            statuses = stDao.getShowSeatStatuses(st.getShowtimeId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (hall != null && seats != null) {
            JPanel mapWrapper = new JPanel(new BorderLayout());
            mapWrapper.setOpaque(false);

            // Screen bar
            JPanel screenPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    g2.setPaint(
                            new GradientPaint(0, 0, new Color(255, 130, 130, 80), w, 0, new Color(255, 130, 130, 30)));
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
            mapWrapper.add(screenPanel, BorderLayout.NORTH);

            // Grid
            int rows = hall.getTotalRows();
            int cols = hall.getTotalCols();
            JPanel grid = new JPanel(new GridLayout(rows, cols + 2, 5, 6));
            grid.setOpaque(false);
            grid.setBorder(new EmptyBorder(16, 10, 16, 10));

            java.util.Map<String, com.rapphim.model.Seat> seatMap = new java.util.HashMap<>();
            for (com.rapphim.model.Seat s : seats) {
                seatMap.put(s.getRowChar() + "" + s.getColNumber(), s);
            }

            for (int r = 1; r <= rows; r++) {
                char rowChar = (char) ('A' + r - 1);
                String rowStr = String.valueOf(rowChar);

                JLabel rl1 = new JLabel(rowStr, SwingConstants.CENTER);
                rl1.setFont(F_BOLD.deriveFont(12f));
                rl1.setForeground(MUTED);
                grid.add(rl1);

                for (int c = 1; c <= cols; c++) {
                    String key = rowStr + c;
                    com.rapphim.model.Seat seat = seatMap.get(key);
                    if (seat != null) {
                        com.rapphim.model.enums.ShowSeatStatus sStatus = statuses != null
                                ? statuses.get(seat.getSeatId())
                                : com.rapphim.model.enums.ShowSeatStatus.AVAILABLE;
                        grid.add(buildSeatButton(seat, sStatus, c));
                    } else {
                        grid.add(new JLabel(""));
                    }
                }

                JLabel rl2 = new JLabel(rowStr, SwingConstants.CENTER);
                rl2.setFont(F_BOLD.deriveFont(12f));
                rl2.setForeground(MUTED);
                grid.add(rl2);
            }
            JScrollPane scrollGrid = new JScrollPane(grid);
            scrollGrid.setBorder(BorderFactory.createEmptyBorder());
            scrollGrid.getViewport().setBackground(Color.WHITE);
            mapWrapper.add(scrollGrid, BorderLayout.CENTER);

            // Legend
            JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
            legend.setOpaque(false);
            legend.add(legendItem(new Color(255, 130, 130), "Phổ thông)"));
            legend.add(legendItem(new Color(255, 255, 0), "Vip "));
            legend.add(legendItem(new Color(156, 163, 175), "Đã bán"));
            legend.add(legendItemImage("/images/icons/wrench.png", "Hỏng"));
            mapWrapper.add(legend, BorderLayout.SOUTH);

            seatView.add(mapWrapper, BorderLayout.CENTER);
        } else {
            JLabel err = new JLabel("Không thể tải sơ đồ ghế.", SwingConstants.CENTER);
            seatView.add(err, BorderLayout.CENTER);
        }

        centerCardPanel.add(seatView, "SEAT_VIEW");
        centerCardLayout.show(centerCardPanel, "SEAT_VIEW");
    }

    private JPanel legendItem(Color color, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);
        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(color);
                g.fillRect(0, 0, 16, 16);
            }
        };
        icon.setPreferredSize(new Dimension(16, 16));
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_NORM.deriveFont(11f));
        lbl.setForeground(TXT);
        item.add(icon);
        item.add(lbl);
        return item;
    }

    private JPanel legendItemImage(String iconPath, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);
        java.net.URL imgUrl = getClass().getResource(iconPath);
        final Image seatImg = (imgUrl != null) ? new ImageIcon(imgUrl).getImage() : null;
        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (seatImg != null)
                    g.drawImage(seatImg, 0, 0, 16, 16, this);
            }
        };
        icon.setPreferredSize(new Dimension(16, 16));
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_NORM.deriveFont(11f));
        lbl.setForeground(TXT);
        item.add(icon);
        item.add(lbl);
        return item;
    }

    private JButton buildSeatButton(com.rapphim.model.Seat seat, com.rapphim.model.enums.ShowSeatStatus sStatus,
            int col) {
        boolean isBroken = seat.isBroken();
        boolean isBooked = sStatus == com.rapphim.model.enums.ShowSeatStatus.BOOKED;

        String iconPath = isBroken ? "/images/icons/wrench.png"
                : (isBooked ? null
                        : (seat.getSeatType() == com.rapphim.model.enums.SeatType.VIP ? "/images/icons/Yellow Chair.png"
                                : "/images/icons/chair.png"));
        java.net.URL imgUrl = iconPath != null ? getClass().getResource(iconPath) : null;
        final Image seatImg = (imgUrl != null) ? new ImageIcon(imgUrl).getImage() : null;

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (seatImg != null) {
                    int w = seatImg.getWidth(null), h = seatImg.getHeight(null);
                    if (w > 0 && h > 0) {
                        double scale = Math.min((double) getWidth() / w, (double) getHeight() / h);
                        int drawW = (int) (w * scale), drawH = (int) (h * scale);
                        int drawX = (getWidth() - drawW) / 2, drawY = (getHeight() - drawH) / 2;
                        g2.drawImage(seatImg, drawX, drawY, drawW, drawH, this);
                    } else {
                        g2.drawImage(seatImg, 0, 0, getWidth(), getHeight(), this);
                    }
                } else {
                    Color bg = isBooked ? new Color(156, 163, 175) : new Color(34, 197, 94);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                if (!isBroken) {
                    g2.setFont(F_BOLD.deriveFont(11f));
                    g2.setColor(Color.WHITE);
                    String text = String.valueOf(col);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(text, x, y - 2);
                }
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setToolTipText(seat.getRowChar() + "" + seat.getColNumber() + (isBooked ? " (Đã đặt)" : " (Trống)"));
        return btn;
    }

    // ════════════ ACTIONS ════════════
    private void selectST(Showtime st) {
        selected = st;
        populateList(searchField != null ? searchField.getText().trim() : "");
        showDetail(st);
    }

    private void handleUpdate(Showtime st) {
        String raw = priceField.getText().replaceAll("[^0-9]", "");
        if (raw.isEmpty()) {
            showMsg("Vui lòng nhập giá hợp lệ!", true);
            return;
        }
        try {
            double np = Double.parseDouble(raw);
            stDao.updateBasePrice(st.getShowtimeId(), np);
            st.setBasePrice(np);
            priceField.setText(NumberFormat.getNumberInstance().format((long) np));
            showMsg("Cập nhật giá thành công!", false);
            populateList(searchField.getText().trim());
        } catch (Exception ex) {
            showMsg("Lỗi: " + ex.getMessage(), true);
        }
    }

    private void handleCancel(Showtime st) {
        LocalDateTime now = LocalDateTime.now();
        if (now.plusDays(5).isAfter(st.getStartTime())) {
            showMsg("Chỉ được hủy suất chiếu ít nhất 5 ngày trước giờ chiếu!", true);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn hủy suất chiếu này?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                stDao.updateStatus(st.getShowtimeId(), ShowtimeStatus.CANCELLED);
                st.setStatus(ShowtimeStatus.CANCELLED);
                showMsg("Hủy suất chiếu thành công!", false);
                updateStatsUI();
                populateList(searchField != null ? searchField.getText().trim() : "");
                showDetail(st);
            } catch (SQLException ex) {
                showMsg("Lỗi: " + ex.getMessage(), true);
            }
        }
    }

    private void handleExport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu file Excel");
        fc.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));
        fc.setSelectedFile(new File("showtimes.xlsx"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".xlsx"))
                f = new File(f.getAbsolutePath() + ".xlsx");
            try {
                com.rapphim.util.ShowtimeExcelUtils.exportToExcel(todayList, movieCache, f);
                showMsg("Đã xuất dữ liệu ra file Excel:\n" + f.getAbsolutePath(), false);
            } catch (Exception ex) {
                showMsg("Lỗi khi xuất: " + ex.getMessage(), true);
            }
        }
    }

    private void handleImport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Excel");
        fc.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            showMsg("Tính năng Import đang phát triển.", false);
        }
    }

    // ════════════ HELPERS ════════════
    private JLabel badge(ShowtimeStatus s) {
        JLabel b = new JLabel(s.getValue());
        b.setFont(F_BOLD.deriveFont(10f));
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(2, 8, 2, 8));
        switch (s) {
            case ONGOING:
                b.setBackground(new Color(209, 250, 229));
                b.setForeground(new Color(6, 95, 70));
                break;
            case SCHEDULED:
                b.setBackground(new Color(254, 243, 199));
                b.setForeground(new Color(146, 64, 14));
                break;
            case CANCELLED:
                b.setBackground(new Color(254, 226, 226));
                b.setForeground(new Color(185, 28, 28));
                break;
            default:
                b.setBackground(new Color(226, 232, 240));
                b.setForeground(new Color(71, 85, 105));
                break;
        }
        return b;
    }

    private JButton mkBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
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
        btn.setFont(F_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
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

    private void showMsg(String msg, boolean err) {
        Color bc = err ? RED : new Color(22, 163, 74);
        JFrame p = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog d = new JDialog(p, "", true);
        d.setUndecorated(true);
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(BG);
        w.setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel i = new JPanel();
        i.setLayout(new BoxLayout(i, BoxLayout.Y_AXIS));
        i.setBackground(Color.WHITE);
        i.setBorder(new CompoundBorder(new LineBorder(bc, 2, true), new EmptyBorder(20, 24, 20, 24)));
        JLabel l = new JLabel("<html>" + msg + "</html>");
        l.setFont(F_NORM);
        l.setForeground(TXT);
        l.setAlignmentX(CENTER_ALIGNMENT);
        JButton ok = mkBtn("OK", bc, bc.darker());
        ok.setAlignmentX(CENTER_ALIGNMENT);
        ok.addActionListener(e -> d.dispose());
        i.add(l);
        i.add(Box.createRigidArea(new Dimension(0, 14)));
        i.add(ok);
        w.add(i);
        d.setContentPane(w);
        d.pack();
        d.setLocationRelativeTo(p);
        d.setVisible(true);
    }
}
