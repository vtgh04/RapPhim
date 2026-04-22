package com.rapphim.view.panels;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.rapphim.util.MovieExcelUtils;

import com.rapphim.dao.MovieDAO;
import com.rapphim.model.Movie;
import com.rapphim.model.enums.MovieStatus;
import com.rapphim.view.dialogs.EditMovieDialog;
import com.rapphim.view.dialogs.AddMovieDialog;

public class MoviePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    Color BG = new Color(240, 242, 245);
    Color WHITE = Color.WHITE;
    Color C_PRIMARY = new Color(30, 30, 35);
    Color C_SECONDARY = new Color(130, 135, 148);
    Color C_BORDER = new Color(228, 228, 228);
    Color C_RED = new Color(220, 38, 38);
    Color C_RED_HOVER = new Color(185, 28, 28);
    Color C_DARK = new Color(30, 30, 35);
    Color C_SURFACE = new Color(249, 250, 251);
    Color C_GRID_LINE = new Color(243, 244, 246);

    // Badge
    Color ACT_BG = new Color(209, 250, 229);
    Color ACT_FG = new Color(6, 95, 70);
    Color INA_BG = new Color(254, 226, 226);
    Color INA_FG = new Color(185, 28, 28);

    Font F_SECTION = new Font("Segoe UI", Font.BOLD, 11);
    Font F_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    Font F_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    Font F_BTN = new Font("Segoe UI", Font.BOLD, 13);
    Font F_SPOTLIGHT = new Font("Segoe UI", Font.BOLD, 11);
    Font F_FEAT_TITLE = new Font("Segoe UI", Font.BOLD, 34);
    Font F_FEAT_DESC = new Font("Segoe UI", Font.PLAIN, 13);

    Font F_SEARCH = new Font("Segoe UI", Font.PLAIN, 13);
    Font F_CARD_META = new Font("Segoe UI", Font.PLAIN, 11);
    Font F_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    Font F_BADGE = new Font("Segoe UI", Font.BOLD, 10);
    Font F_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    Font F_TH = new Font("Segoe UI", Font.BOLD, 12);
    Font F_PILL = new Font("Segoe UI", Font.BOLD, 11);

    String PLACEHOLDER = "Tìm kiếm theo tên, thể loại hoặc mã phim...";

    private List<Movie> allMovies = new ArrayList<>();
    private List<Movie> activeMovies = new ArrayList<>();
    private int featuredIdx = 0;
    private boolean gridMode = true;
    private static final Map<String, BufferedImage> posterCache = new HashMap<>();

    // Components updated dynamically
    private JPanel bannerHolder;

    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JButton gridBtn, tableBtn;
    private JPanel listHolder;

    public MoviePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        loadData();

        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(BG);
        content.add(buildNorth(), BorderLayout.NORTH);
        content.add(buildCenter(), BorderLayout.CENTER);
        content.add(buildSouth(), BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(BG);
        add(scroll, BorderLayout.CENTER);

        renderList();
    }

    // ════════════════════════════════════════════════════════════════════════
    // DATA
    // ════════════════════════════════════════════════════════════════════════
    private void loadData() {
        try {
            allMovies = new MovieDAO().findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            allMovies = new ArrayList<>();
        }
        activeMovies = allMovies.stream()
                .filter(m -> m.getStatus() == MovieStatus.ACTIVE)
                .collect(Collectors.toList());
        featuredIdx = 0;
    }

    // ════════════════════════════════════════════════════════════════════════
    // NORTH — header bar
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildNorth() {
        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(BG);
        north.setBorder(new EmptyBorder(28, 32, 16, 32));

        // Left: labels
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblSection = new JLabel(" QUẢN LÝ PHIM");
        lblSection.setIcon(loadIcon("images/icons/film.png", 14, 14));
        lblSection.setFont(F_SECTION);
        lblSection.setForeground(C_RED);

        JLabel lblTitle = new JLabel("Danh sách phim");
        lblTitle.setFont(F_TITLE);
        lblTitle.setForeground(C_PRIMARY);

        JLabel lblSub = new JLabel("Quản lý danh sách phim.");
        lblSub.setFont(F_SUBTITLE);
        lblSub.setForeground(C_SECONDARY);

        left.add(lblSection);
        left.add(Box.createRigidArea(new Dimension(0, 3)));
        left.add(lblTitle);
        left.add(Box.createRigidArea(new Dimension(0, 2)));
        left.add(lblSub);

        // Right: buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnExport = roundBtn(" Export Excel", new Color(243, 244, 246), C_PRIMARY, new Color(229, 231, 235),
                140);
        btnExport.addActionListener(e -> handleExportExcel());

        JButton btnImport = roundBtn(" Import Excel", new Color(243, 244, 246), C_PRIMARY, new Color(229, 231, 235),
                140);
        btnImport.addActionListener(e -> handleImportExcel());

        JButton btnAdd = roundBtn(" Add New Movie", C_RED, WHITE, C_RED_HOVER, 160);
        btnAdd.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(MoviePanel.this);
            AddMovieDialog dlg = new AddMovieDialog(parentFrame);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                loadData();
                renderList();
                refreshBanner();
            }
        });

        right.add(btnExport);
        right.add(btnImport);
        right.add(btnAdd);

        north.add(left, BorderLayout.WEST);
        north.add(right, BorderLayout.EAST);
        return north;
    }

    // ════════════════════════════════════════════════════════════════════════
    // CENTER — banner + info boxes
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(0, 32, 16, 32));

        // pBanner
        bannerHolder = new JPanel(new BorderLayout());
        bannerHolder.setOpaque(false);
        bannerHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshBanner();
        center.add(bannerHolder);

        return center;
    }

    private void refreshBanner() {
        bannerHolder.removeAll();
        if (activeMovies.isEmpty()) {
            bannerHolder.revalidate();
            bannerHolder.repaint();
            return;
        }

        Movie m = activeMovies.get(featuredIdx);
        BufferedImage poster = loadImage(m.getPosterUrl());

        JPanel bannerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                if (w <= 0 || h <= 0)
                    return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.clip(new RoundRectangle2D.Float(0, 0, w, h, 22, 22));

                if (poster != null) {
                    double scale = Math.max((double) w / poster.getWidth(),
                            (double) h / poster.getHeight());
                    int sw = (int) (poster.getWidth() * scale);
                    int sh = (int) (poster.getHeight() * scale);
                    g2.drawImage(poster, w - sw, (h - sh) / 2, sw, sh, null);
                } else {
                    g2.setColor(new Color(40, 40, 50));
                    g2.fillRect(0, 0, w, h);
                }

                // dark gradient left → transparent right
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(15, 15, 25, 245),
                        (float) (w * 0.70), 0, new Color(15, 15, 25, 20)));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        bannerPanel.setOpaque(true);
        bannerPanel.setPreferredSize(new Dimension(0, 240));
        bannerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        bannerPanel.setBorder(new EmptyBorder(32, 36, 32, 36));

        // text overlay
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel spotlightLbl = new JLabel(" FEATURED SPOTLIGHT");
        spotlightLbl.setIcon(loadIcon("images/icons/star.png", 13, 13));
        spotlightLbl.setFont(F_SPOTLIGHT);
        spotlightLbl.setForeground(new Color(251, 191, 36));
        spotlightLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(m.getTitle());
        titleLbl.setFont(F_FEAT_TITLE);
        titleLbl.setForeground(WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        String desc = m.getDescription() != null ? m.getDescription() : "";
        JLabel descLbl = new JLabel("<html><body style='width:420px'>" + desc + "</body></html>");
        descLbl.setFont(F_FEAT_DESC);
        descLbl.setForeground(new Color(195, 198, 215));
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // nav buttons
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nav.setOpaque(false);

        JButton btnPrev = iconBtn(loadIcon("images/icons/left-arrow.png", 16, 16));
        JButton btnNext = iconBtn(loadIcon("images/icons/right-arrow.png", 16, 16));

        btnPrev.addActionListener(e -> {
            featuredIdx = (featuredIdx - 1 + activeMovies.size()) % activeMovies.size();
            refreshBanner();
        });
        btnNext.addActionListener(e -> {
            featuredIdx = (featuredIdx + 1) % activeMovies.size();
            refreshBanner();
        });

        nav.add(btnPrev);
        nav.add(btnNext);
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(spotlightLbl);
        text.add(Box.createRigidArea(new Dimension(0, 10)));
        text.add(titleLbl);
        text.add(Box.createRigidArea(new Dimension(0, 10)));
        text.add(descLbl);
        text.add(Box.createRigidArea(new Dimension(0, 18)));
        text.add(nav);

        bannerPanel.add(text, BorderLayout.WEST);
        bannerHolder.add(bannerPanel, BorderLayout.CENTER);
        bannerHolder.revalidate();
        bannerHolder.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // SOUTH — search + filter + grid
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildSouth() {
        JPanel south = new JPanel(new BorderLayout(0, 12));
        south.setBackground(BG);
        south.setBorder(new EmptyBorder(0, 32, 28, 32));

        JPanel filterBar = new RoundedPanel(12, WHITE);
        filterBar.setLayout(new BoxLayout(filterBar, BoxLayout.X_AXIS));
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, C_BORDER),
                new EmptyBorder(8, 16, 8, 16)));
        filterBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        // search icon
        JLabel searchIco = new JLabel("🔍 ");
        searchIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchIco.setForeground(C_SECONDARY);

        // search field
        searchField = new JTextField(PLACEHOLDER);
        searchField.setFont(F_SEARCH);
        searchField.setForeground(C_SECONDARY);
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setOpaque(false);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(C_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText(PLACEHOLDER);
                    searchField.setForeground(C_SECONDARY);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                renderList();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                renderList();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                renderList();
            }
        });

        // filter combo
        JLabel filterIco = new JLabel();
        ImageIcon filterIcon = loadIcon("images/icons/filter.png", 16, 16);
        if (filterIcon != null)
            filterIco.setIcon(filterIcon);
        else {
            filterIco.setText("  ▾  ");
            filterIco.setFont(F_SEARCH);
        }
        filterIco.setForeground(C_SECONDARY);
        filterIco.setBorder(new EmptyBorder(0, 8, 0, 4));

        filterCombo = new JComboBox<>(new String[] { "All", "Active", "Inactive" });
        filterCombo.setFont(F_SEARCH);
        filterCombo.setBackground(WHITE);
        filterCombo.setPreferredSize(new Dimension(140, 30));
        filterCombo.setMaximumSize(new Dimension(140, 30));
        filterCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterCombo.addActionListener(e -> renderList());

        // toggle buttons — with icons
        gridBtn = toggleBtn("Grid", loadIcon("images/icons/grid.png", 15, 15), true);
        tableBtn = toggleBtn("Table", loadIcon("images/icons/table.png", 15, 15), false);
        gridBtn.addActionListener(e -> {
            if (!gridMode) {
                gridMode = true;
                syncToggle();
                renderList();
            }
        });
        tableBtn.addActionListener(e -> {
            if (gridMode) {
                gridMode = false;
                syncToggle();
                renderList();
            }
        });

        JPanel toggle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toggle.setOpaque(false);
        toggle.setBorder(new RoundedBorder(8, C_BORDER));
        toggle.add(gridBtn);
        toggle.add(tableBtn);

        filterBar.add(searchIco);
        filterBar.add(searchField);
        filterBar.add(Box.createHorizontalGlue());
        filterBar.add(filterIco);
        filterBar.add(filterCombo);
        filterBar.add(Box.createRigidArea(new Dimension(12, 0)));
        filterBar.add(toggle);

        listHolder = new JPanel(new BorderLayout());
        listHolder.setOpaque(false);

        south.add(filterBar, BorderLayout.NORTH);
        south.add(listHolder, BorderLayout.CENTER);
        return south;
    }

    // ════════════════════════════════════════════════════════════════════════
    // RENDER LIST
    // ════════════════════════════════════════════════════════════════════════
    private void renderList() {
        List<Movie> filtered = filtered();
        listHolder.removeAll();
        listHolder.add(gridMode ? buildGrid(filtered) : buildTable(filtered), BorderLayout.CENTER);
        listHolder.revalidate();
        listHolder.repaint();
    }

    private List<Movie> filtered() {
        String q = searchField.getText().trim();
        if (q.equals(PLACEHOLDER))
            q = "";
        String ql = q.toLowerCase();
        String sel = (String) filterCombo.getSelectedItem();

        final String finalQl = ql;
        return allMovies.stream().filter(m -> {
            if (sel != null && !sel.equals("All")) {
                boolean wantActive = sel.equalsIgnoreCase("Active");
                if (wantActive != (m.getStatus() == MovieStatus.ACTIVE))
                    return false;
            }
            return finalQl.isEmpty()
                    || m.getTitle().toLowerCase().contains(finalQl)
                    || m.getGenre().toLowerCase().contains(finalQl)
                    || m.getMovieId().toLowerCase().contains(finalQl);
        }).collect(Collectors.toList());
    }

    private JPanel buildGrid(List<Movie> movies) {
        final int COLS = 5;
        JPanel grid = new JPanel(new GridLayout(0, COLS, 16, 16));
        grid.setOpaque(false);

        if (movies.isEmpty()) {
            JLabel empty = new JLabel("No movies found.");
            empty.setFont(F_SUBTITLE);
            empty.setForeground(C_SECONDARY);
            grid.add(empty);
        } else {
            movies.forEach(m -> grid.add(movieCard(m)));
            // fill remaining cells to keep sizing even
            int remainder = movies.size() % COLS;
            if (remainder != 0) {
                for (int i = 0; i < COLS - remainder; i++) {
                    JPanel filler = new JPanel();
                    filler.setOpaque(false);
                    grid.add(filler);
                }
            }
        }
        return grid;
    }

    private JPanel movieCard(Movie movie) {
        BufferedImage poster = loadImage(movie.getPosterUrl());

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                if (w <= 0 || h <= 0)
                    return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.clip(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));

                if (poster != null) {
                    double sc = Math.max((double) w / poster.getWidth(),
                            (double) h / poster.getHeight());
                    int sw = (int) (poster.getWidth() * sc);
                    int sh = (int) (poster.getHeight() * sc);
                    g2.drawImage(poster, (w - sw) / 2, (h - sh) / 2, sw, sh, null);
                } else {
                    g2.setColor(new Color(55, 55, 65));
                    g2.fillRect(0, 0, w, h);
                }
                // bottom gradient
                g2.setPaint(new GradientPaint(0, (float) (h * 0.42f),
                        new Color(0, 0, 0, 0), 0, h, new Color(0, 0, 0, 215)));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 310));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setOpaque(false);
        overlay.setBorder(new EmptyBorder(10, 12, 14, 12));

        // top badges
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        String lang = movie.getLanguage() != null ? movie.getLanguage() : "N/A";
        boolean act = movie.getStatus() == MovieStatus.ACTIVE;
        String rating = movie.getRating();

        // LEFT: [lang badge] [rating badge] side by side
        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftBadges.setOpaque(false);
        leftBadges.add(badge(lang, new Color(99, 102, 241), WHITE));
        if (rating != null && !rating.isEmpty()) {
            leftBadges.add(ratingBadge(rating));
        }
        topRow.add(leftBadges, BorderLayout.WEST);

        // RIGHT: status badge
        topRow.add(badge(act ? "ACTIVE" : "INACTIVE", act ? ACT_BG : INA_BG,
                act ? ACT_FG : INA_FG), BorderLayout.EAST);

        // bottom info
        JPanel bot = new JPanel();
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        bot.setOpaque(false);

        JLabel metaLbl = new JLabel(movie.getDurationMins() + " MIN  •  " + movie.getGenre().toUpperCase());
        metaLbl.setFont(F_CARD_META);
        metaLbl.setForeground(new Color(175, 178, 200));
        metaLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("<html><body style='width:170px'>" + movie.getTitle() + "</body></html>");
        titleLbl.setFont(F_CARD_TITLE);
        titleLbl.setForeground(WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        bot.add(metaLbl);
        bot.add(Box.createRigidArea(new Dimension(0, 4)));
        bot.add(titleLbl);

        overlay.add(topRow, BorderLayout.NORTH);
        overlay.add(bot, BorderLayout.SOUTH);
        card.add(overlay, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(99, 102, 241, 160), 2, true));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(null);
                card.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(MoviePanel.this);
                EditMovieDialog dlg = new EditMovieDialog(parentFrame, movie);
                dlg.setVisible(true);
                if (dlg.isSaved()) {
                    loadData();
                    renderList();
                    refreshBanner();
                }
            }
        });

        return card;
    }

    private JLabel badge(String text, Color bg, Color fg) {
        JLabel b = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BADGE);
        b.setForeground(fg);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(3, 7, 3, 7));
        return b;
    }

    private JLabel ratingBadge(String rating) {
        Color[] rc = ratingColor(rating);
        Color rbg = rc[0], rfg = rc[1];
        JLabel b = new JLabel(rating) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(rbg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(rfg);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(rfg);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(2, 6, 2, 6));
        return b;
    }

    /** Returns [backgroundColor, foregroundColor] for a rating code */
    private Color[] ratingColor(String rating) {
        if (rating == null)
            return new Color[] { new Color(107, 114, 128), Color.WHITE };
        switch (rating.toUpperCase()) {
            case "P":
                return new Color[] { new Color(34, 197, 94), new Color(20, 83, 45) }; // Green
            case "K":
                return new Color[] { new Color(59, 130, 246), Color.WHITE }; // Blue
            case "T13":
                return new Color[] { new Color(234, 179, 8), new Color(92, 67, 3) }; // Yellow
            case "T16":
                return new Color[] { new Color(249, 115, 22), Color.WHITE }; // Orange
            case "T18":
                return new Color[] { new Color(220, 38, 38), Color.WHITE }; // Red
            default:
                return new Color[] { new Color(107, 114, 128), Color.WHITE }; // Gray
        }
    }

    private static final String[] COLS = { "", "TITLE", "GENRE", "DURATION", "LANGUAGE", "STATUS", "RELEASE DATE" };
    private static final int[] CWS = { 60, 200, 120, 90, 80, 110, 110 };

    private JPanel buildTable(List<Movie> movies) {
        JPanel wrap = new RoundedPanel(14, WHITE);
        wrap.setLayout(new BorderLayout());
        wrap.setBorder(new RoundedBorder(14, C_BORDER));

        DefaultTableModel model = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (Movie m : movies) {
            model.addRow(new Object[] {
                    m.getPosterUrl(), m.getTitle(), m.getGenre(),
                    m.getDurationMins() + " min", m.getLanguage(),
                    m.getStatus().getValue(),
                    m.getReleaseDate() != null ? m.getReleaseDate().toString() : ""
            });
        }

        JTable tbl = new JTable(model);
        tbl.setFont(F_TABLE);
        tbl.setRowHeight(58);
        tbl.setShowHorizontalLines(true);
        tbl.setShowVerticalLines(false);
        tbl.setGridColor(C_GRID_LINE);
        tbl.setBackground(WHITE);
        tbl.setSelectionBackground(new Color(239, 246, 255));
        tbl.setSelectionForeground(C_PRIMARY);
        tbl.setIntercellSpacing(new Dimension(0, 1));
        tbl.setFillsViewportHeight(true);

        JTableHeader th = tbl.getTableHeader();
        th.setFont(F_TH);
        th.setForeground(C_SECONDARY);
        th.setBackground(C_SURFACE);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_GRID_LINE));
        th.setPreferredSize(new Dimension(0, 44));
        th.setReorderingAllowed(false);

        DefaultTableCellRenderer centerH = new DefaultTableCellRenderer();
        centerH.setHorizontalAlignment(SwingConstants.CENTER);
        centerH.setFont(F_TH);
        centerH.setForeground(C_SECONDARY);
        centerH.setBackground(C_SURFACE);

        for (int i = 0; i < COLS.length; i++) {
            tbl.getColumnModel().getColumn(i).setHeaderRenderer(centerH);
            tbl.getColumnModel().getColumn(i).setPreferredWidth(CWS[i]);
        }

        // poster
        tbl.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = new JLabel();
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(sel ? t.getSelectionBackground() : WHITE);
                if (v != null) {
                    BufferedImage img = loadImage(v.toString());
                    if (img != null)
                        lbl.setIcon(new ImageIcon(img.getScaledInstance(38, 54, Image.SCALE_SMOOTH)));
                }
                return lbl;
            }
        });

        // centered text
        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 6, 0, 6));
                setFont(F_TABLE);
                setForeground(C_PRIMARY);
                if (!sel)
                    setBackground(WHITE);
                return this;
            }
        };
        for (int i = 1; i < COLS.length; i++) {
            if (i != 5)
                tbl.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // status pill
        tbl.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                String s = v != null ? v.toString() : "";
                boolean a = s.equals("ACTIVE");
                JLabel pill = new JLabel(s) {
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                pill.setFont(F_PILL);
                pill.setHorizontalAlignment(SwingConstants.CENTER);
                pill.setOpaque(false);
                pill.setBorder(new EmptyBorder(4, 10, 4, 10));
                pill.setBackground(a ? ACT_BG : INA_BG);
                pill.setForeground(a ? ACT_FG : INA_FG);
                JPanel wp = new JPanel(new GridBagLayout());
                wp.setOpaque(true);
                wp.setBackground(sel ? t.getSelectionBackground() : WHITE);
                wp.add(pill);
                return wp;
            }
        });

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(WHITE);
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOGGLE SYNC
    // ════════════════════════════════════════════════════════════════════════
    private void syncToggle() {
        gridBtn.setBackground(gridMode ? C_DARK : WHITE);
        gridBtn.setForeground(gridMode ? WHITE : C_SECONDARY);
        tableBtn.setBackground(!gridMode ? C_DARK : WHITE);
        tableBtn.setForeground(!gridMode ? WHITE : C_SECONDARY);
        gridBtn.repaint();
        tableBtn.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private JButton roundBtn(String text, Color bg, Color fg, Color hover, int w) {
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
        btn.setFont(F_BTN);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, 40));
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

    /** Icon-only circular red button for banner navigation. */
    private JButton iconBtn(ImageIcon icon) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        if (icon != null)
            btn.setIcon(icon);
        btn.setBackground(C_RED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 40));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(C_RED_HOVER);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(C_RED);
                btn.repaint();
            }
        });
        return btn;
    }

    private JButton toggleBtn(String text, ImageIcon icon, boolean active) {
        JButton btn = new JButton(text) {
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
        if (icon != null) {
            btn.setIcon(icon);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setIconTextGap(5);
        }
        btn.setFont(F_BTN);
        btn.setForeground(active ? WHITE : C_SECONDARY);
        btn.setBackground(active ? C_DARK : WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 32));
        return btn;
    }

    private BufferedImage loadImage(String path) {
        if (path == null || path.isBlank())
            return null;
        if (path.startsWith("/"))
            path = path.substring(1);
        // Return cached image if available
        if (posterCache.containsKey(path))
            return posterCache.get(path);
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null)
            return null;
        Image src = new ImageIcon(url).getImage();
        int w = src.getWidth(null), h = src.getHeight(null);
        if (w <= 0 || h <= 0)
            return null;
        // Scale down large images to save memory (max 600px wide)
        int maxW = 600;
        if (w > maxW) {
            int newH = (int) ((double) h / w * maxW);
            w = maxW;
            h = newH;
        }
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        posterCache.put(path, bi);
        return bi;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        if (path == null || path.isBlank())
            return null;
        if (path.startsWith("/"))
            path = path.substring(1);
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null)
            return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // ════════════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ════════════════════════════════════════════════════════════════════════
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int r, Color bg) {
            this.radius = r;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
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

    /**
     * Modern minimal scrollbar — thin rounded thumb, no track border, no arrow
     * buttons.
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int THUMB_W = 8;
        private static final Color THUMB_COLOR = new Color(180, 185, 195);
        private static final Color THUMB_HOVER_COLOR = new Color(140, 145, 155);
        private static final Color TRACK_COLOR = new Color(240, 242, 245);
        private boolean hovered = false;

        @Override
        protected void installListeners() {
            super.installListeners();
            scrollbar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    scrollbar.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    scrollbar.repaint();
                }
            });
        }

        @Override
        protected void configureScrollBarColors() {
            trackColor = TRACK_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(TRACK_COLOR);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled())
                return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? THUMB_HOVER_COLOR : THUMB_COLOR);
            // center the thin thumb inside the track
            int xOff = r.x + (r.width - THUMB_W) / 2;
            g2.fillRoundRect(xOff, r.y + 2, THUMB_W, r.height - 4, THUMB_W, THUMB_W);
            g2.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(THUMB_W, 40);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // EXPORT / IMPORT HANDLERS
    // ════════════════════════════════════════════════════════════════════════
    private void handleExportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("Movies.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xlsx");
            }
            try {
                MovieExcelUtils.exportToExcel(allMovies, fileToSave);
                JOptionPane.showMessageDialog(this, "Đã xuất dữ liệu ra file Excel:\n" + fileToSave.getAbsolutePath(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file:\n" + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleImportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = fileChooser.getSelectedFile();
            try {
                List<Movie> importedList = MovieExcelUtils.importFromExcel(fileToRead, new MovieDAO());
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + importedList.size() + " phim!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();

                renderList();
                refreshBanner();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi đọc file:\n" + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // MAIN
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Movie Panel");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1280, 860);
            f.setLocationRelativeTo(null);
            f.add(new MoviePanel());
            f.setVisible(true);
        });
    }
}
