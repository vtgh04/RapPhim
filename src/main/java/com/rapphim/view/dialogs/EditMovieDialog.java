package com.rapphim.view.dialogs;

import com.rapphim.dao.MovieDAO;
import com.rapphim.model.Movie;
import com.rapphim.model.enums.MovieStatus;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class EditMovieDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color BG_COLOR = new Color(248, 249, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_HINT = new Color(160, 165, 175);
    private static final Color BORDER_COLOR = new Color(218, 222, 233);
    private static final Color PRIMARY_RED = new Color(220, 38, 38);
    private static final Color HOVER_RED = new Color(185, 28, 28);
    private static final Color CANCEL_BG = new Color(243, 244, 246);
    private static final Color CANCEL_HOVER = new Color(229, 231, 235);
    private static final Color CANCEL_TEXT = new Color(55, 65, 81);
    private static final Color AUTO_BG = new Color(238, 242, 255);
    private static final Color AUTO_TEXT = new Color(99, 102, 241);
    private static final Color CLOSE_NORMAL = new Color(160, 165, 175);
    private static final Color CLOSE_HOVER = new Color(220, 38, 38);
    private static final Color SUCCESS_BG = new Color(240, 253, 244);
    private static final Color SUCCESS_FG = new Color(22, 163, 74);
    private static final Color WARN_COLOR = new Color(220, 38, 38);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_WARN = new Font("Segoe UI", Font.ITALIC, 11);

    private JTextField txtMovieId;
    private JTextField txtTitle;
    private JTextField txtGenre;
    private JComboBox<String> cmbRating;
    private JSpinner spnDuration;
    private JComboBox<String> cmbFormat;
    private JComboBox<String> cmbLanguage;
    private JDateChooser dateReleaseDate;
    private JComboBox<String> cmbStatus;
    private JTextArea txtDescription;
    private JTextField txtPosterUrl;

    private JLabel warnTitle;
    private JLabel warnGenre;
    private JLabel warnDuration;
    private JLabel warnDate;
    private JLabel warnDesc;
    private JLabel warnPoster;
    private JLabel lblSystemError;

    private final MovieDAO movieDAO = new MovieDAO();
    private final Movie movie;
    private boolean saved = false;

    public EditMovieDialog(JFrame parent, Movie movie) {
        super(parent, "Edit Movie", true);
        this.movie = movie;
        initUI();
        populateFields();
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(520, 800);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setUndecorated(true);
        getContentPane().setBackground(BG_COLOR);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(WHITE);
        main.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_COLOR),
                new EmptyBorder(28, 32, 28, 32)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lbTitle = new JLabel("Chỉnh sửa phim");
        lbTitle.setFont(FONT_TITLE);
        lbTitle.setForeground(TEXT_PRIMARY);
        header.add(lbTitle, BorderLayout.WEST);
        header.add(createCloseButton(), BorderLayout.EAST);
        main.add(header);
        main.add(Box.createRigidArea(new Dimension(0, 8)));

        // separator
        JPanel sep = new JPanel();
        sep.setBackground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(sep);
        main.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Movie ID (read-only) ─────────────────────────────────────────
        txtMovieId = styledField("");
        txtMovieId.setEditable(false);
        txtMovieId.setBackground(AUTO_BG);
        txtMovieId.setForeground(AUTO_TEXT);
        txtMovieId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        main.add(fieldRow("Movie ID", txtMovieId, null));

        // ── Title ────────────────────────────────────────────────────────
        txtTitle = styledField("");
        warnTitle = createWarnLabel();
        main.add(fieldRow("Tên phim *", txtTitle, warnTitle));

        // ── Genre + Rating ────────────────────────────────────────────────
        txtGenre = styledField("Click để chọn thể loại...");
        txtGenre.setEditable(false);
        txtGenre.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        txtGenre.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showGenrePopup(txtGenre);
            }
        });

        cmbRating = new JComboBox<>(new String[] { "P", "K", "T13", "T16", "T18" });
        cmbRating.setFont(FONT_INPUT);
        cmbRating.setBackground(WHITE);
        cmbRating.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        warnGenre = createWarnLabel();
        main.add(twoColRow("Thể loại *", txtGenre, "Phân loại *", cmbRating, warnGenre, null));

        // ── Duration + Format ─────────────────────────────────────────────
        // Duration: phải > 90 (min trong model = 1 để không cắt dữ liệu cũ, validate
        // riêng)
        spnDuration = new JSpinner(new SpinnerNumberModel(120, 1, 600, 1));
        spnDuration.setFont(FONT_INPUT);

        cmbFormat = new JComboBox<>(new String[] { "2D", "3D", "IMAX" });
        cmbFormat.setFont(FONT_INPUT);
        cmbFormat.setBackground(WHITE);
        // Trong Edit, Format không cho đổi để giữ tính nhất quán
        cmbFormat.setEnabled(false);

        warnDuration = createWarnLabel();
        main.add(twoColRow("Thời lượng (phút) *", spnDuration, "Định dạng", cmbFormat, warnDuration, null));

        // ── Language + Release Date ───────────────────────────────────────
        cmbLanguage = new JComboBox<>(new String[] { "2D Lồng tiếng", "2D Phụ đề" });
        cmbLanguage.setFont(FONT_INPUT);
        cmbLanguage.setBackground(WHITE);
        cmbLanguage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        dateReleaseDate = new JDateChooser();
        dateReleaseDate.setDateFormatString("yyyy-MM-dd");
        dateReleaseDate.setFont(FONT_INPUT);
        dateReleaseDate.setBackground(WHITE);

        warnDate = createWarnLabel();
        main.add(twoColRow("Ngôn ngữ *", cmbLanguage, "Ngày phát hành *", dateReleaseDate, null, warnDate));

        // ── Status ───────────────────────────────────────────────────────
        cmbStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        cmbStatus.setFont(FONT_INPUT);
        cmbStatus.setBackground(WHITE);
        cmbStatus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        main.add(fieldRow("Trạng thái *", cmbStatus, null));

        // ── Description ──────────────────────────────────────────────────
        txtDescription = new JTextArea(3, 20);
        txtDescription.setFont(FONT_INPUT);
        txtDescription.setForeground(TEXT_HINT);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        warnDesc = createWarnLabel();
        main.add(fieldRow("Mô tả *", descScroll, warnDesc));

        // ── Poster URL ────────────────────────────────────────────────────
        JPanel posterPanel = new JPanel(new BorderLayout(5, 0));
        posterPanel.setOpaque(false);
        posterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPosterUrl = styledField("");
        JButton btnBrowse = new JButton("Browse");
        btnBrowse.setFont(FONT_LABEL);
        btnBrowse.setBackground(AUTO_BG);
        btnBrowse.setForeground(AUTO_TEXT);
        btnBrowse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBrowse.setPreferredSize(new Dimension(80, 42));
        btnBrowse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(
                    new File("src/main/resources/images/movies").getAbsolutePath());
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                String path = f.getAbsolutePath();
                int idx = path.indexOf("images\\movies");
                if (idx != -1)
                    path = path.substring(idx).replace("\\", "/");
                txtPosterUrl.setText(path);
                txtPosterUrl.setForeground(TEXT_PRIMARY);
            }
        });
        posterPanel.add(txtPosterUrl, BorderLayout.CENTER);
        posterPanel.add(btnBrowse, BorderLayout.EAST);
        warnPoster = createWarnLabel();
        main.add(fieldRow("Poster URL *", posterPanel, warnPoster));

        main.add(Box.createRigidArea(new Dimension(0, 4)));

        // Lỗi hệ thống
        lblSystemError = new JLabel(" ");
        lblSystemError.setFont(FONT_WARN);
        lblSystemError.setForeground(WARN_COLOR);
        lblSystemError.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(lblSystemError);
        main.add(Box.createRigidArea(new Dimension(0, 8)));

        main.add(buttonRow());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(main, BorderLayout.CENTER);
        setContentPane(wrapper);
    }

    // =====================================================================
    // POPULATE
    // =====================================================================
    private void populateFields() {
        txtMovieId.setText(movie.getMovieId());

        if (movie.getTitle() != null) {
            txtTitle.setText(movie.getTitle());
            txtTitle.setForeground(TEXT_PRIMARY);
        }
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            txtGenre.setText(movie.getGenre());
            txtGenre.setForeground(TEXT_PRIMARY);
        }
        if (movie.getRating() != null)
            cmbRating.setSelectedItem(movie.getRating());

        spnDuration.setValue(movie.getDurationMins() > 0 ? movie.getDurationMins() : 120);

        if (movie.getFormatMovie() != null)
            cmbFormat.setSelectedItem(movie.getFormatMovie());

        syncLanguageWithFormat();
        if (movie.getLanguage() != null)
            cmbLanguage.setSelectedItem(movie.getLanguage());

        if (movie.getReleaseDate() != null)
            dateReleaseDate.setDate(java.sql.Date.valueOf(movie.getReleaseDate()));

        cmbStatus.setSelectedItem(movie.getStatus() == MovieStatus.ACTIVE ? "Active" : "Inactive");

        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            txtDescription.setText(movie.getDescription());
            txtDescription.setForeground(TEXT_PRIMARY);
        }
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            txtPosterUrl.setText(movie.getPosterUrl());
            txtPosterUrl.setForeground(TEXT_PRIMARY);
        }
    }

    // =====================================================================
    // FIELD BUILDERS
    // =====================================================================
    private JPanel fieldRow(String label, Component input, JLabel warn) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, warn != null ? 88 : 72));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 5)));

        if (input instanceof JTextField)
            ((JTextField) input).setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        ((JComponent) input).setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(input);
        if (warn != null) {
            warn.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(warn);
        }
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        return p;
    }

    private JPanel twoColRow(String lbl1, JComponent f1, String lbl2, JComponent f2,
            JLabel warnLeft, JLabel warnRight) {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setOpaque(false);
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setOpaque(false);
        JLabel l1 = new JLabel(lbl1);
        l1.setFont(FONT_LABEL);
        l1.setForeground(TEXT_PRIMARY);
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        f1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f1.setAlignmentX(Component.LEFT_ALIGNMENT);
        p1.add(l1);
        p1.add(Box.createRigidArea(new Dimension(0, 5)));
        p1.add(f1);
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        row.add(p1, gbc);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setOpaque(false);
        JLabel l2 = new JLabel(lbl2);
        l2.setFont(FONT_LABEL);
        l2.setForeground(TEXT_PRIMARY);
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        f2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f2.setAlignmentX(Component.LEFT_ALIGNMENT);
        p2.add(l2);
        p2.add(Box.createRigidArea(new Dimension(0, 5)));
        p2.add(f2);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(p2, gbc);

        outer.add(row);

        if (warnLeft != null || warnRight != null) {
            JPanel warnRow = new JPanel(new GridLayout(1, 2, 12, 0));
            warnRow.setOpaque(false);
            warnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            warnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
            warnRow.add(warnLeft != null ? warnLeft : new JLabel(" "));
            warnRow.add(warnRight != null ? warnRight : new JLabel(" "));
            outer.add(warnRow);
        }
        outer.add(Box.createRigidArea(new Dimension(0, 4)));
        return outer;
    }

    private void showGenrePopup(JTextField field) {
        JPopupMenu popup = new JPopupMenu();
        String[] genres = { "Action", "Adventure", "Animation", "Comedy", "Crime",
                "Drama", "Family", "Fantasy", "Horror", "Romance", "Sci-Fi", "Thriller" };
        String current = field.getText();
        for (String g : genres) {
            JCheckBox cb = new JCheckBox(g);
            cb.setSelected(current.contains(g));
            cb.setBackground(WHITE);
            cb.setFont(FONT_INPUT);
            cb.addActionListener(e -> {
                List<String> selected = new ArrayList<>();
                for (Component c : popup.getComponents()) {
                    if (c instanceof JCheckBox && ((JCheckBox) c).isSelected())
                        selected.add(((JCheckBox) c).getText());
                }
                field.setText(String.join(", ", selected));
                field.setForeground(TEXT_PRIMARY);
                if (warnGenre != null)
                    warnGenre.setText(" ");
            });
            popup.add(cb);
        }
        popup.show(field, 0, field.getHeight());
    }

    private JPanel buttonRow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        JButton cancelBtn = roundedBtn("Hủy", CANCEL_BG, CANCEL_TEXT, CANCEL_HOVER);
        cancelBtn.addActionListener(e -> dispose());
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        panel.add(cancelBtn, gbc);

        JButton saveBtn = roundedBtn("Lưu thay đổi", PRIMARY_RED, WHITE, HOVER_RED);
        saveBtn.addActionListener(e -> handleUpdate());
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(saveBtn, gbc);

        return panel;
    }

    // =====================================================================
    // COMPONENT FACTORY
    // =====================================================================
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
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_HINT);
        field.setText(placeholder);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(WHITE);
        return field;
    }

    private JLabel createWarnLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(FONT_WARN);
        lbl.setForeground(WARN_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        return lbl;
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
        btn.setForeground(CLOSE_NORMAL);
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
                btn.setForeground(CLOSE_HOVER);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(CLOSE_NORMAL);
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
        btn.setFont(FONT_BTN);
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

    // =====================================================================
    // LOGIC
    // =====================================================================
    private void handleUpdate() {
        clearWarnings();
        boolean hasError = false;

        // — Title —
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            warnTitle.setText("Vui lòng nhập tên phim.");
            hasError = true;
        } else if (!title.matches("[\\p{L}\\p{N}\\s]+")) {
            warnTitle.setText("Tên phim chứa ký tự không hợp lệ.");
            hasError = true;
        }

        // — Genre —
        String genre = txtGenre.getText().trim();
        if (genre.isEmpty() || genre.equals("Click để chọn thể loại...")) {
            warnGenre.setText("Vui lòng chọn ít nhất một thể loại.");
            hasError = true;
        }

        // — Duration: phải > 90 —
        int duration = (int) spnDuration.getValue();
        if (duration <= 90) {
            warnDuration.setText("Thời lượng phải lớn hơn 90 phút.");
            hasError = true;
        }

        // — Release Date —
        if (dateReleaseDate.getDate() == null) {
            warnDate.setText("Vui lòng chọn ngày phát hành.");
            hasError = true;
        }

        // — Description —
        String desc = txtDescription.getText().trim();
        if (desc.isEmpty()) {
            warnDesc.setText("Vui lòng nhập mô tả phim.");
            hasError = true;
        }

        // — Poster URL —
        String poster = txtPosterUrl.getText().trim();
        if (poster.isEmpty()) {
            warnPoster.setText("Vui lòng chọn hoặc nhập đường dẫn poster.");
            hasError = true;
        }

        if (hasError)
            return;

        // — Build model —
        String format = (String) cmbFormat.getSelectedItem();
        String language = (String) cmbLanguage.getSelectedItem();
        String rating = (String) cmbRating.getSelectedItem();
        String statusSel = (String) cmbStatus.getSelectedItem();
        LocalDate releaseDate = dateReleaseDate.getDate()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDurationMins(duration);
        movie.setFormatMovie(format);
        movie.setRating(rating);
        movie.setLanguage(language);
        movie.setReleaseDate(releaseDate);
        movie.setStatus("Active".equalsIgnoreCase(statusSel) ? MovieStatus.ACTIVE : MovieStatus.INACTIVE);
        movie.setDescription(desc);
        movie.setPosterUrl(poster);

        try {
            movieDAO.update(movie);
            saved = true;
            showSuccessDialog("Cập nhật thành công!", movie.getTitle());
            dispose();
        } catch (Exception ex) {
            lblSystemError.setText("<html>Lỗi hệ thống: " + ex.getMessage() + "</html>");
            ex.printStackTrace();
        }
    }

    /** Dialog thông báo thành công, đồng bộ với bộ màu thiết kế. */
    private void showSuccessDialog(String headline, String movieTitle) {
        JDialog dlg = new JDialog(this, "", true);
        dlg.setUndecorated(true);
        dlg.setSize(340, 220);
        dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, new Color(34, 197, 74, 60)),
                new EmptyBorder(30, 32, 24, 32)));

        // Icon tick
        JLabel ico = new JLabel("✓") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SUCCESS_BG);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ico.setFont(new Font("Segoe UI", Font.BOLD, 24));
        ico.setForeground(SUCCESS_FG);
        ico.setHorizontalAlignment(SwingConstants.CENTER);
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        ico.setPreferredSize(new Dimension(52, 52));
        ico.setMaximumSize(new Dimension(52, 52));
        panel.add(ico);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        JLabel lbHead = new JLabel(headline);
        lbHead.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbHead.setForeground(SUCCESS_FG);
        lbHead.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbHead);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lbSub = new JLabel(
                "<html><div style='text-align:center'>\"" + movieTitle
                        + "\"<br>đã được cập nhật thành công.</div></html>");
        lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbSub.setForeground(new Color(100, 110, 120));
        lbSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbSub.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbSub);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton okBtn = roundedBtn("OK", SUCCESS_FG, WHITE, new Color(16, 130, 55));
        okBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.addActionListener(e -> dlg.dispose());
        panel.add(okBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_COLOR);
        wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        wrap.add(panel, BorderLayout.CENTER);
        dlg.setContentPane(wrap);
        dlg.setVisible(true);
    }

    private void syncLanguageWithFormat() {
        if (cmbFormat == null || cmbLanguage == null)
            return;
        String format = (String) cmbFormat.getSelectedItem();
        String prev = (String) cmbLanguage.getSelectedItem();
        cmbLanguage.removeAllItems();
        cmbLanguage.addItem(format + " Lồng tiếng");
        cmbLanguage.addItem(format + " Phụ đề");
        if (prev != null && prev.contains(format.substring(0, 1)))
            cmbLanguage.setSelectedItem(prev);
    }

    private void clearWarnings() {
        warnTitle.setText(" ");
        warnGenre.setText(" ");
        warnDuration.setText(" ");
        warnDate.setText(" ");
        warnDesc.setText(" ");
        warnPoster.setText(" ");
        lblSystemError.setText(" ");
    }

    // =====================================================================
    // INNER CLASS
    // =====================================================================
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
