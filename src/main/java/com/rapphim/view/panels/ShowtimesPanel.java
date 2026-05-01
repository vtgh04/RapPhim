package com.rapphim.view.panels;

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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ShowtimesPanel extends JPanel {

    private static final Color BG = new Color(240, 242, 245);
    private static final Color TXT = new Color(25, 25, 35);
    private static final Color MUTED = new Color(120, 125, 140);
    private static final Color BORDER = new Color(225, 228, 235);
    private static final Color RED = new Color(220, 38, 38);

    private static final Font F_NORM = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_MONO = new Font("Consolas", Font.BOLD, 18);

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ShowtimeDAO stDao = new ShowtimeDAO();
    private final MovieDAO mvDao = new MovieDAO();
    private Map<String, Movie> movieCache = new HashMap<>();
    private List<Showtime> todayList = new ArrayList<>();
    private Showtime selected;
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
                selected = null;
                populateList("");
                showEmpty();
            }
        });
    }

    private void loadData() {
        try {
            todayList = stDao.findTodayShowtimes();
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
        JLabel t2 = new JLabel("Quản lý suất chiếu trong ngày hôm nay");
        t2.setFont(F_NORM);
        t2.setForeground(MUTED);
        tb.add(t1);
        tb.add(t2);
        tr.add(tb, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton btnExport = mkBtn(" Export Excel", new Color(243, 244, 246), new Color(229, 231, 235));
        btnExport.setForeground(TXT);
        btnExport.addActionListener(e -> handleExport());
        btnRow.add(btnExport);

        JButton btnImport = mkBtn(" Import Excel", new Color(243, 244, 246), new Color(229, 231, 235));
        btnImport.setForeground(TXT);
        btnImport.addActionListener(e -> handleImport());
        btnRow.add(btnImport);

        JButton btnAdd = mkBtn(" Tạo suất chiếu", RED, new Color(185, 28, 28));
        btnAdd.setPreferredSize(new Dimension(180, 38));
        btnAdd.addActionListener(e -> showMsg("Tính năng đang phát triển.", false));
        btnRow.add(btnAdd);

        tr.add(btnRow, BorderLayout.EAST);
        n.add(tr, BorderLayout.NORTH);

        // Stats row
        JPanel sr = new JPanel(new GridLayout(1, 3, 14, 0));
        sr.setOpaque(false);
        int total = 0, sched = 0, halls = 0;
        try {
            total = stDao.countAll();
            sched = stDao.countByStatus(ShowtimeStatus.SCHEDULED);
            halls = stDao.countActiveHalls();
        } catch (SQLException ignored) {
        }
        sr.add(statCard("Tổng suất chiếu", total, new Color(59, 130, 246)));
        sr.add(statCard("Đã lên lịch", sched, new Color(245, 158, 11)));
        sr.add(statCard("Phòng hoạt động", halls, new Color(16, 185, 129)));
        n.add(sr, BorderLayout.CENTER);

        // Search row
        JPanel sRow = new JPanel(new BorderLayout(8, 0));
        sRow.setOpaque(false);
        sRow.setBorder(new EmptyBorder(10, 0, 0, 0));
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
        sRow.add(new JLabel("🔍"), BorderLayout.WEST);
        sRow.add(searchField, BorderLayout.CENTER);
        n.add(sRow, BorderLayout.SOUTH);

        return n;
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
        JPanel ctr = new JPanel(new BorderLayout(12, 0));
        ctr.setOpaque(false);

        // pLeft scrolls independently
        ctr.add(buildLeft(), BorderLayout.CENTER);

        // pRight (detailPanel) is fixed/sticky – not inside any scroll
        JPanel rightWrap = buildRight();
        rightWrap.setPreferredSize(new Dimension(320, 0));
        ctr.add(rightWrap, BorderLayout.EAST);
        return ctr;
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
        detailPanel.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(18, 18, 18, 18)));
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
        JPanel info = new JPanel(new GridLayout(0, 2, 6, 6));
        info.setBackground(new Color(248, 249, 252));
        info.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(12, 14, 12, 14)));
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        infoRow(info, "Movie:", mv != null ? mv.getTitle() : st.getMovieId());
        infoRow(info, "Hall:", "HALL " + st.getHallId().replaceAll("[^0-9]", ""));
        infoRow(info, "Language:", mv != null ? mv.getLanguage() : "N/A");
        infoRow(info, "Rating:", mv != null ? mv.getRating() : "N/A");
        infoRow(info, "Duration:", mv != null ? mv.getDurationMins() + " mins" : "N/A");
        infoRow(info, "Start:", st.getStartTime().format(TF));
        infoRow(info, "End:", st.getEndTime().format(TF));
        infoRow(info, "Date:", st.getStartTime().format(DF));
        infoRow(info, "Status:", st.getStatus().getValue());

        detailPanel.add(info);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 14)));

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

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private void infoRow(JPanel p, String k, String v) {
        JLabel kl = new JLabel(k);
        kl.setFont(F_NORM.deriveFont(11f));
        kl.setForeground(MUTED);
        JLabel vl = new JLabel(v);
        vl.setFont(F_BOLD);
        vl.setForeground(TXT);
        p.add(kl);
        p.add(vl);
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
