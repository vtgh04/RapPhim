package com.rapphim.view.panels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;

import com.rapphim.model.Movie;
import com.rapphim.model.Showtime;
import com.rapphim.model.Seat;
import com.rapphim.model.enums.MovieStatus;
import com.rapphim.model.enums.ShowSeatStatus;
import com.rapphim.model.enums.ShowtimeStatus;
import com.rapphim.dao.MovieDAO;
import com.rapphim.dao.ShowtimeDAO;
import com.rapphim.dao.HallDao;
import com.rapphim.dao.InvoiceDAO;
import com.rapphim.view.dialogs.PaymentDialog;

public class SalePanel extends JPanel {

    // Colors
    private static final Color BG_MAIN = new Color(248, 249, 252);
    private static final Color BG_PANEL = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TXT_DARK = new Color(15, 23, 42);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color PRIMARY = new Color(220, 38, 38); // Red
    private static final Color GREEN_COLOR = new Color(52, 191, 36);

    // Fonts
    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_NORM = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_H1 = new Font("Segoe UI", Font.BOLD, 20);

    // Layout components
    private CardLayout cardLayout;
    private JPanel leftCardPanel;
    private JPanel rightPanel;

    // View Panels
    private JPanel movieView;
    private JPanel showtimeView;
    private JPanel showtimeGrid;
    private JPanel movieInfoContainer;
    private JPanel seatView;

    // Cart Components
    private JPanel cartItemsPanel;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    private JLabel lblTotal;
    private JTextField txtPromo;
    private JTextField searchField;

    // Data
    private MovieDAO movieDao = new MovieDAO();
    private ShowtimeDAO showtimeDao = new ShowtimeDAO();
    private HallDao hallDao = new HallDao();
    private InvoiceDAO invoiceDao = new InvoiceDAO();

    // State
    private Movie selectedMovie;
    private Showtime selectedShowtime;
    private java.util.List<Seat> selectedSeats = new ArrayList<>();
    private Map<Seat, Double> cartMap = new LinkedHashMap<>();

    public SalePanel() {
        setLayout(new GridBagLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        initComponents();
        loadMovies();
    }

    private void initComponents() {
        // --- LEFT PANEL (CardLayout) ---
        cardLayout = new CardLayout();
        leftCardPanel = new JPanel(cardLayout);
        leftCardPanel.setOpaque(false);

        movieView = new JPanel(new BorderLayout());
        movieView.setOpaque(false);
        showtimeView = new JPanel(new BorderLayout());
        showtimeView.setOpaque(false);
        seatView = new JPanel(new BorderLayout());
        seatView.setOpaque(false);

        leftCardPanel.add(buildMovieView(), "MOVIE");
        leftCardPanel.add(buildShowtimeView(), "SHOWTIME");
        leftCardPanel.add(buildSeatView(), "SEAT");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 16);
        add(leftCardPanel, gbc);

        rightPanel = buildRightPanel();

        gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(rightPanel, gbc);
    }

    // MOVIE VIEW
    private JPanel buildMovieView() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_PANEL);
        wrap.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(20, 20, 20, 20)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("SELECT MOIVE");
        title.setFont(F_H1);
        title.setForeground(TXT_DARK);
        header.add(title, BorderLayout.WEST);

        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search movies...");
        searchField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                loadMovies();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                loadMovies();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                loadMovies();
            }
        });

        JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
        searchWrap.setBackground(Color.WHITE);
        searchWrap.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(4, 8, 4, 8)));

        try {
            java.net.URL iconUrl = getClass().getResource("/images/icons/search.png");
            if (iconUrl != null) {
                Image img = new ImageIcon(iconUrl).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                searchWrap.add(new JLabel(new ImageIcon(img)), BorderLayout.WEST);
            }
        } catch (Exception e) {
        }

        searchWrap.add(searchField, BorderLayout.CENTER);
        header.add(searchWrap, BorderLayout.EAST);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        wrap.add(header, BorderLayout.NORTH);

        movieView.setLayout(new GridLayout(0, 5, 16, 16));
        movieView.setBackground(BG_PANEL);

        JPanel movieWrapper = new JPanel(new BorderLayout());
        movieWrapper.setBackground(BG_PANEL);
        movieWrapper.add(movieView, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(movieWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        wrap.add(scroll, BorderLayout.CENTER);

        return wrap;
    }

    private void loadMovies() {
        movieView.removeAll();
        try {
            java.util.List<Movie> movies = movieDao.findAll();
            java.util.List<Showtime> sts = showtimeDao.findAll();
            java.time.LocalDate today = java.time.LocalDate.now();

            java.util.Set<String> validMovieIds = new java.util.HashSet<>();
            for (Showtime st : sts) {
                if (st.getStatus() == ShowtimeStatus.SCHEDULED) {
                    if (!st.getStartTime().toLocalDate().isBefore(today)) {
                        validMovieIds.add(st.getMovieId());
                    }
                }
            }

            String keyword = searchField != null ? searchField.getText().trim().toLowerCase() : "";

            for (Movie m : movies) {
                if (m.getStatus() == MovieStatus.ACTIVE && validMovieIds.contains(m.getMovieId())) {
                    if (keyword.isEmpty() || m.getTitle().toLowerCase().contains(keyword)) {
                        movieView.add(createMovieCard(m));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        movieView.revalidate();
        movieView.repaint();
    }

    private JPanel createMovieCard(Movie m) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 240));
        card.setBackground(Color.LIGHT_GRAY);
        card.setBorder(new LineBorder(BORDER, 1, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Placeholder for poster (Normally load from m.getPosterUrl())
        JLabel lblImg = new JLabel("<html><center>" + m.getTitle() + "</center></html>", SwingConstants.CENTER);
        try {
            java.net.URL url = getClass().getResource("/" + m.getPosterUrl());
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(160, 200, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(img));
                lblImg.setText("");
            }
        } catch (Exception e) {
        }
        card.add(lblImg, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(8, 8, 8, 8));
        JLabel title = new JLabel(m.getTitle());
        title.setFont(F_BOLD.deriveFont(12f));
        JLabel genre = new JLabel(m.getGenre());
        genre.setFont(F_NORM.deriveFont(10f));
        genre.setForeground(MUTED);
        info.add(title);
        info.add(genre);
        card.add(info, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedMovie = m;
                loadShowtimes(m);
                cardLayout.show(leftCardPanel, "SHOWTIME");
            }
        });
        return card;
    }

    // ================== SHOWTIME VIEW ==================
    private JPanel buildShowtimeView() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_PANEL);
        wrap.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(20, 20, 20, 20)));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        header.setOpaque(false);
        JButton btnBack = new JButton("<- SELECT SHOWTIME");
        btnBack.setFont(F_H1);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setForeground(TXT_DARK);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> cardLayout.show(leftCardPanel, "MOVIE"));
        header.add(btnBack);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        wrap.add(header, BorderLayout.NORTH);

        showtimeView.setLayout(new BorderLayout(0, 24));
        showtimeView.setBackground(BG_PANEL);

        movieInfoContainer = new JPanel(new BorderLayout(24, 0));
        movieInfoContainer.setOpaque(false);

        showtimeGrid = new JPanel(new GridLayout(0, 3, 16, 16));
        showtimeGrid.setBackground(BG_PANEL);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(BG_PANEL);
        gridWrapper.add(showtimeGrid, BorderLayout.NORTH);

        showtimeView.add(movieInfoContainer, BorderLayout.NORTH);
        showtimeView.add(gridWrapper, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(showtimeView);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        wrap.add(scroll, BorderLayout.CENTER);

        return wrap;
    }

    private void loadShowtimes(Movie m) {
        showtimeGrid.removeAll();
        movieInfoContainer.removeAll();

        // 1. Setup Movie Info Panel — modern clean look
        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(140, 200));
        try {
            java.net.URL url = getClass().getResource("/" + m.getPosterUrl());
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(140, 200, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
        }

        // Details: center aligned
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTitle = new JLabel(m.getTitle(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TXT_DARK);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblMeta = new JLabel(m.getDurationMins() + " min  •  " + m.getRating());
        lblMeta.setFont(F_NORM);
        lblMeta.setForeground(MUTED);
        lblMeta.setAlignmentX(LEFT_ALIGNMENT);

        JTextArea txtDesc = new JTextArea(m.getDescription() != null ? m.getDescription() : "");
        txtDesc.setFont(F_NORM.deriveFont(12f));
        txtDesc.setForeground(MUTED);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setBorder(new EmptyBorder(10, 0, 0, 0));
        txtDesc.setAlignmentX(LEFT_ALIGNMENT);

        detailsPanel.add(lblTitle);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        detailsPanel.add(lblMeta);
        detailsPanel.add(txtDesc);

        // Wrap with gray background + subtle border
        JPanel infoBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        infoBox.setOpaque(false);
        infoBox.setBackground(new Color(248, 249, 252));
        infoBox.setBorder(new EmptyBorder(1, 1, 1, 1)); // space for border
        infoBox.add(lblImg, BorderLayout.WEST);
        infoBox.add(detailsPanel, BorderLayout.CENTER);
        movieInfoContainer.add(infoBox, BorderLayout.CENTER);

        // 2. Load Showtimes
        try {
            java.util.List<Showtime> sts = showtimeDao.findAll();
            java.time.LocalDate today = java.time.LocalDate.now();

            for (Showtime st : sts) {
                if (st.getMovieId().equals(m.getMovieId()) && st.getStatus() == ShowtimeStatus.SCHEDULED) {
                    if (!st.getStartTime().toLocalDate().isBefore(today)) {
                        showtimeGrid.add(createShowtimeCard(st));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        movieInfoContainer.revalidate();
        movieInfoContainer.repaint();
        showtimeGrid.revalidate();
        showtimeGrid.repaint();
    }

    private JPanel createShowtimeCard(Showtime st) {
        Color defaultBg = Color.WHITE;
        Color hoverBg = new Color(250, 250, 253);

        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                // Draw custom border matching the state
                boolean isHover = getBackground().equals(hoverBg);
                g2.setColor(isHover ? PRIMARY : BORDER);
                g2.setStroke(new BasicStroke(isHover ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(defaultBg);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top: Date badge
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
            }
        };
        badgePanel.setOpaque(false);
        badgePanel.setBackground(new Color(226, 232, 240));
        badgePanel.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel lblDate = new JLabel(st.getStartTime().format(df));
        lblDate.setFont(F_NORM.deriveFont(10f));
        lblDate.setForeground(MUTED);
        badgePanel.add(lblDate);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.add(badgePanel);
        card.add(topRow, BorderLayout.NORTH);

        // Center: Time (big)
        java.time.format.DateTimeFormatter tf = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = st.getStartTime().format(tf) + " - " + st.getEndTime().format(tf);
        JLabel lblTime = new JLabel(timeStr, SwingConstants.LEFT);
        lblTime.setFont(F_BOLD.deriveFont(20f));
        lblTime.setForeground(TXT_DARK);
        card.add(lblTime, BorderLayout.CENTER);

        // Bottom: Hall + Price
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);
        JLabel lblHall = new JLabel("HALL " + st.getHallId().replaceAll("[^0-9]", ""));
        lblHall.setFont(F_NORM.deriveFont(11f));
        lblHall.setForeground(MUTED);
        JLabel lblPrice = new JLabel(String.format("%,.0fk", st.getBasePrice() / 1000));
        lblPrice.setFont(F_BOLD.deriveFont(16f));
        lblPrice.setForeground(TXT_DARK);
        bot.add(lblHall, BorderLayout.WEST);
        bot.add(lblPrice, BorderLayout.EAST);
        card.add(bot, BorderLayout.SOUTH);

        // Hover + Click
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedShowtime = st;
                loadSeats(st);
                cardLayout.show(leftCardPanel, "SEAT");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverBg);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(defaultBg);
                card.repaint();
            }
        });
        return card;
    }

    // ================== SEAT VIEW ==================
    private JPanel buildSeatView() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_PANEL);
        wrap.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(20, 20, 20, 20)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton btnBack = new JButton("<- SELECT SEATS");
        btnBack.setFont(F_H1);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setForeground(TXT_DARK);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> cardLayout.show(leftCardPanel, "SHOWTIME"));
        header.add(btnBack, BorderLayout.WEST);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        legend.setOpaque(false);
        legend.add(legendItemImage("/images/icons/chair.png", "Phổ thông"));
        legend.add(legendItemImage("/images/icons/Yellow Chair.png", "VIP"));
        legend.add(legendItem(new Color(156, 163, 175), "Đã đặt"));
        legend.add(legendItemImage("/images/icons/wrench.png", "Hỏng"));
        legend.add(legendItem(PRIMARY, "Đang chọn"));
        header.add(legend, BorderLayout.EAST);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        wrap.add(header, BorderLayout.NORTH);

        seatView.setLayout(new GridBagLayout());
        seatView.setBackground(BG_PANEL);
        JScrollPane scroll = new JScrollPane(seatView);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        wrap.add(scroll, BorderLayout.CENTER);

        return wrap;
    }

    private JPanel legendItem(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(c);
                g.fillRoundRect(0, 0, 14, 14, 4, 4);
            }
        };
        icon.setPreferredSize(new Dimension(14, 14));
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_NORM.deriveFont(11f));
        lbl.setForeground(TXT_DARK);
        p.add(icon);
        p.add(lbl);
        return p;
    }

    private JPanel legendItemImage(String iconPath, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        java.net.URL iconUrl = getClass().getResource(iconPath);
        JLabel icon = new JLabel();
        if (iconUrl != null) {
            Image img = new ImageIcon(iconUrl).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            icon.setIcon(new ImageIcon(img));
        }
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_NORM.deriveFont(11f));
        lbl.setForeground(TXT_DARK);
        p.add(icon);
        p.add(lbl);
        return p;
    }

    private void loadSeats(Showtime st) {
        seatView.removeAll();
        selectedSeats.clear();
        cartMap.clear();
        updateCartUI();

        try {
            com.rapphim.model.CinemaHall hall = hallDao.findHallById(st.getHallId());
            java.util.List<Seat> seats = hallDao.findSeatsByHall(st.getHallId());
            Map<String, ShowSeatStatus> statuses = showtimeDao.getShowSeatStatuses(st.getShowtimeId());

            if (hall != null && seats != null) {
                int rows = hall.getTotalRows();
                int cols = hall.getTotalCols();

                GridBagConstraints gbc = new GridBagConstraints();

                // Add Screen at the top
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridwidth = cols;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(0, 0, 40, 0);

                JPanel screenPanel = new JPanel(new BorderLayout());
                screenPanel.setOpaque(false);
                JLabel screenLbl = new JLabel("MÀN HÌNH", SwingConstants.CENTER);
                screenLbl.setFont(F_BOLD.deriveFont(14f));
                screenLbl.setForeground(MUTED);
                screenLbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(3, 0, 0, 0, MUTED),
                        new EmptyBorder(8, 0, 0, 0)));
                screenPanel.add(screenLbl, BorderLayout.CENTER);
                seatView.add(screenPanel, gbc);

                // Reset constraints for seats
                gbc.gridwidth = 1;
                gbc.fill = GridBagConstraints.NONE;
                gbc.insets = new Insets(4, 6, 4, 6);

                Map<String, Seat> seatMap = new HashMap<>();
                for (Seat s : seats)
                    seatMap.put(s.getRowChar() + "" + s.getColNumber(), s);

                for (int r = 1; r <= rows; r++) {
                    // Left Row Label
                    gbc.gridx = 0;
                    gbc.gridy = r;
                    gbc.insets = new Insets(4, 0, 4, 20); // Gap after label
                    JLabel leftLbl = new JLabel(String.valueOf((char) ('A' + r - 1)));
                    leftLbl.setFont(F_BOLD.deriveFont(14f));
                    leftLbl.setForeground(MUTED);
                    seatView.add(leftLbl, gbc);

                    // Reset insets for seats
                    gbc.insets = new Insets(4, 6, 4, 6);

                    for (int c = 1; c <= cols; c++) {
                        String key = (char) ('A' + r - 1) + String.valueOf(c);
                        Seat s = seatMap.get(key);
                        if (s != null) {
                            ShowSeatStatus status = statuses != null ? statuses.get(s.getSeatId())
                                    : ShowSeatStatus.AVAILABLE;
                            gbc.gridx = c;
                            gbc.gridy = r;
                            seatView.add(createSeatButton(s, status, st.getBasePrice()), gbc);
                        }
                    }

                    // Right Row Label
                    gbc.gridx = cols + 1;
                    gbc.gridy = r;
                    gbc.insets = new Insets(4, 20, 4, 0); // Gap before label
                    JLabel rightLbl = new JLabel(String.valueOf((char) ('A' + r - 1)));
                    rightLbl.setFont(F_BOLD.deriveFont(14f));
                    rightLbl.setForeground(MUTED);
                    seatView.add(rightLbl, gbc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        seatView.revalidate();
        seatView.repaint();
    }

    private JButton createSeatButton(Seat s, ShowSeatStatus status, double basePrice) {
        boolean isBroken = s.isBroken();
        boolean isBooked = status == ShowSeatStatus.BOOKED;
        boolean isVip = s.getSeatType() == com.rapphim.model.enums.SeatType.VIP;

        // Icon logic: wrench > booked(gray box) > chair image
        String iconPath = isBroken ? "/images/icons/wrench.png"
                : (isBooked ? null
                        : (isVip ? "/images/icons/Yellow Chair.png" : "/images/icons/chair.png"));
        java.net.URL imgUrl = iconPath != null ? getClass().getResource(iconPath) : null;
        final Image seatImg = (imgUrl != null) ? new ImageIcon(imgUrl).getImage() : null;

        // Track selected state
        final boolean[] selected = { false };

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (selected[0]) {
                    // Highlight selected seat with red overlay
                    g2.setColor(new Color(220, 38, 38, 180));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }

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
                    // Booked: gray rounded rect
                    g2.setColor(new Color(156, 163, 175));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                // Draw col number in black on the image
                if (!isBroken) {
                    g2.setFont(F_BOLD.deriveFont(10f));
                    g2.setColor(Color.BLACK);
                    String text = String.valueOf(s.getColNumber());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(text, x, y - 2);
                }
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(38, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setToolTipText(s.getRowChar() + "" + s.getColNumber()
                + (isBroken ? " (Hỏng)" : isBooked ? " (Đã đặt)" : isVip ? " VIP" : " Phổ thông"));

        if (!isBroken && !isBooked) {
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                selected[0] = !selected[0];
                if (selected[0]) {
                    selectedSeats.add(s);
                    cartMap.put(s, basePrice * s.getSeatFactor());
                } else {
                    selectedSeats.remove(s);
                    cartMap.remove(s);
                }
                updateCartUI();
                btn.repaint();
            });
        }
        return btn;
    }

    // ================== CART RIGHT PANEL ==================
    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(20, 20, 20, 20)));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblOrder = new JLabel("Giỏ hàng");
        lblOrder.setFont(F_H1);
        lblOrder.setForeground(TXT_DARK);
        header.add(lblOrder, BorderLayout.WEST);

        JButton btnReset = new JButton("Reset"); // Could be an icon
        btnReset.setContentAreaFilled(false);
        btnReset.addActionListener(e -> {
            selectedSeats.clear();
            cartMap.clear();
            if (selectedShowtime != null)
                loadSeats(selectedShowtime);
            updateCartUI();
        });
        header.add(btnReset, BorderLayout.EAST);

        JPanel topWrap = new JPanel(new BorderLayout(0, 16));
        topWrap.setOpaque(false);
        topWrap.add(header, BorderLayout.NORTH);
        p.add(topWrap, BorderLayout.NORTH);

        // Cart Items
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(BG_PANEL);
        JScrollPane scroll = new JScrollPane(cartItemsPanel);
        scroll.setBorder(new EmptyBorder(16, 0, 16, 0));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);

        // Bottom Totals & Payment
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        // Promo
        JPanel promoContainer = new JPanel();
        promoContainer.setLayout(new BoxLayout(promoContainer, BoxLayout.Y_AXIS));
        promoContainer.setOpaque(false);
        promoContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPromoTitle = new JLabel("Khuyến mãi");
        lblPromoTitle.setFont(F_BOLD.deriveFont(12f));
        lblPromoTitle.setForeground(TXT_DARK);
        lblPromoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel promoWrap = new JPanel(new BorderLayout(8, 0));
        promoWrap.setOpaque(false);
        promoWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPromo = new JTextField();
        txtPromo.putClientProperty("JTextField.placeholderText", "Mã khuyến mãi");
        JButton btnApply = new JButton("APPLY");
        btnApply.setForeground(Color.WHITE);
        btnApply.setBackground(PRIMARY);
        btnApply.setOpaque(true);
        btnApply.setContentAreaFilled(true);
        btnApply.setBorderPainted(false);
        btnApply.setFocusPainted(false);
        promoWrap.add(txtPromo, BorderLayout.CENTER);
        promoWrap.add(btnApply, BorderLayout.EAST);

        promoContainer.add(lblPromoTitle);
        promoContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        promoContainer.add(promoWrap);
        promoContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        bottom.add(promoContainer);
        bottom.add(Box.createRigidArea(new Dimension(0, 16)));

        // Totals
        lblSubtotal = new JLabel("$0.00");
        lblTax = new JLabel("$0.00");
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(F_H1);
        lblTotal.setForeground(PRIMARY);

        bottom.add(totalRow("Tạm tính", lblSubtotal, F_NORM, TXT_DARK));
        bottom.add(totalRow("Thuế (10%)", lblTax, F_NORM, MUTED));
        bottom.add(Box.createRigidArea(new Dimension(0, 8)));
        bottom.add(totalRow("Tổng cộng", lblTotal, F_H1, TXT_DARK));
        bottom.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnCheckout = new JButton("Thanh toán");
        btnCheckout.setBackground(GREEN_COLOR);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(F_BOLD);
        btnCheckout.setOpaque(true);
        btnCheckout.setContentAreaFilled(true);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnCheckout.setFocusPainted(false);
        btnCheckout.addActionListener(e -> handleCheckoutClick());
        bottom.add(btnCheckout);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void handleCheckoutClick() {
        if (selectedSeats.isEmpty() || selectedShowtime == null) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống hoặc chưa chọn suất chiếu!");
            return;
        }

        double subtotal = 0;
        for (Double price : cartMap.values())
            subtotal += price;
        double tax = subtotal * 0.10;
        double total = subtotal + tax;

        PaymentDialog dialog = new PaymentDialog(SwingUtilities.getWindowAncestor(this), total);
        dialog.setVisible(true);

        String method = dialog.getSelectedMethod();
        if (method == null) {
            return;
        }

        if ("CARD".equals(method)) {

            int choice = JOptionPane.showConfirmDialog(this,
                    "Đang chờ khách quẹt thẻ trên máy POS...\nKhách đã quẹt thẻ thành công chưa?",
                    "Máy POS Quẹt Thẻ", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return; // Khách hủy hoặc quẹt lỗi
            }
        } else if ("TRANSFER".equals(method)) {
            com.rapphim.view.dialogs.TransferDialog transferDialog = new com.rapphim.view.dialogs.TransferDialog(
                    SwingUtilities.getWindowAncestor(this), total);
            transferDialog.setVisible(true);
            if (!transferDialog.isPaid()) {
                return; // Thu ngân xác nhận chưa nhận được tiền hoặc Hủy
            }
        }

        try {
            boolean success = invoiceDao.processCheckout(
                    selectedShowtime.getShowtimeId(),
                    cartMap,
                    total,
                    method,
                    "CONFIRMED");

            if (success) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                selectedSeats.clear();
                cartMap.clear();
                updateCartUI();
                loadSeats(selectedShowtime);
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại, vui lòng thử lại!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage());
        }
    }

    private JPanel totalRow(String title, JLabel val, Font f, Color c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t = new JLabel(title);
        t.setFont(f);
        t.setForeground(c);
        p.add(t, BorderLayout.WEST);
        val.setFont(f);
        p.add(val, BorderLayout.EAST);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return p;
    }

    private void updateCartUI() {
        cartItemsPanel.removeAll();
        double subtotal = 0;

        if (cartMap.isEmpty()) {
            JLabel empty = new JLabel("GIỎ HÀNG TRỐNG", SwingConstants.CENTER);
            empty.setForeground(new Color(203, 213, 225));
            empty.setFont(F_BOLD);
            empty.setAlignmentX(CENTER_ALIGNMENT);
            cartItemsPanel.add(Box.createVerticalGlue());
            cartItemsPanel.add(empty);
            cartItemsPanel.add(Box.createVerticalGlue());
        } else {
            for (Map.Entry<Seat, Double> entry : cartMap.entrySet()) {
                Seat s = entry.getKey();
                Double price = entry.getValue();
                subtotal += price;

                cartItemsPanel.add(buildTicketCard(s, price));
                cartItemsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Gap between cards
            }
        }

        double tax = subtotal * 0.10;
        double total = subtotal + tax;

        lblSubtotal.setText(String.format("%,.0f đ", subtotal));
        lblTax.setText(String.format("%,.0f đ", tax));
        lblTotal.setText(String.format("%,.0f đ", total));

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel buildTicketCard(Seat s, double price) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        // Icon Box
        JPanel iconBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 247, 255)); // Light blue bg
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(40, 40));

        JLabel lblIcon = new JLabel();
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            java.net.URL url = getClass().getResource("/images/icons/sales.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                lblIcon.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
        }
        iconBox.add(lblIcon, BorderLayout.CENTER);

        // Info Box
        JPanel infoBox = new JPanel(new GridLayout(2, 1, 0, 2));
        infoBox.setOpaque(false);

        String movieTitle = selectedMovie != null ? selectedMovie.getTitle() : "Movie";
        String seatType = s.getSeatType() == com.rapphim.model.enums.SeatType.VIP ? "VIP" : "Phổ thông";
        String titleStr = movieTitle + " - Seat " + s.getRowChar() + s.getColNumber() + " (" + seatType + ")";

        JLabel lblTitle = new JLabel(titleStr);
        lblTitle.setFont(F_BOLD.deriveFont(12f));
        lblTitle.setForeground(TXT_DARK);

        JLabel lblDesc = new JLabel(String.format("1 X %,.0f đ", price));
        lblDesc.setFont(F_NORM.deriveFont(11f));
        lblDesc.setForeground(MUTED);

        infoBox.add(lblTitle);
        infoBox.add(lblDesc);

        card.add(iconBox, BorderLayout.WEST);
        card.add(infoBox, BorderLayout.CENTER);

        return card;
    }
}
