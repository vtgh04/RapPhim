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
    // ── Design tokens ────────────────────────────────────────────────────────
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
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 14);
    // ── Fields ───────────────────────────────────────────────────────────────
    private JTextField txtMovieId;
    private JTextField txtTitle;
    private JTextField txtGenre;
    private JSpinner spnDuration;
    private JComboBox<String> cmbFormat;
    private JComboBox<String> cmbLanguage;
    private JDateChooser dateReleaseDate;
    private JComboBox<String> cmbStatus;
    private JTextArea txtDescription;
    private JTextField txtPosterUrl;
    private JLabel lblError;
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

    // =====================================================================
    // UI
    // =====================================================================
    private void initUI() {
        setSize(520, 750);
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
        // ── Header ──────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel title = new JLabel("Edit Movie");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        JButton closeBtn = createCloseButton();
        header.add(closeBtn, BorderLayout.EAST);
        main.add(header);
        main.add(Box.createRigidArea(new Dimension(0, 8)));
        // separator
        JPanel sep = new JPanel();
        sep.setBackground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(sep);
        main.add(Box.createRigidArea(new Dimension(0, 18)));
        // ── Form fields ─────────────────────────────────────────────────
        // Movie ID (read-only)
        txtMovieId = styledField("");
        txtMovieId.setEditable(false);
        txtMovieId.setBackground(AUTO_BG);
        txtMovieId.setForeground(AUTO_TEXT);
        txtMovieId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        main.add(fieldRow("Movie ID", txtMovieId));
        // Title
        txtTitle = styledField("");
        main.add(fieldRow("Title", txtTitle));
        // Genre row
        txtGenre = styledField("Click to select genres...");
        txtGenre.setEditable(false);
        txtGenre.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        txtGenre.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showGenrePopup(txtGenre);
            }
        });
        main.add(fieldRow("Genre", txtGenre));

        // Duration + Type side-by-side
        spnDuration = new JSpinner(new SpinnerNumberModel(120, 1, 600, 1));
        spnDuration.setFont(FONT_INPUT);
        spnDuration.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        spnDuration.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        cmbFormat = new JComboBox<>(new String[] { "2D", "3D", "IMAX" });
        cmbFormat.setFont(FONT_INPUT);
        cmbFormat.setBackground(WHITE);
        cmbFormat.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbFormat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbFormat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        main.add(twoColRow("Duration (min)", spnDuration, "Type", cmbFormat));
        main.add(Box.createRigidArea(new Dimension(0, 10)));

        // Language/Type + Release Date side-by-side
        cmbLanguage = new JComboBox<>(new String[] { "2D Lồng tiếng", "2D Phụ đề", "3D Lồng tiếng", "3D Phụ đề",
                "IMAX Lồng tiếng", "IMAX Phụ đề" });
        cmbLanguage.setFont(FONT_INPUT);
        cmbLanguage.setBackground(WHITE);
        cmbLanguage.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbLanguage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbLanguage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        dateReleaseDate = new JDateChooser();
        dateReleaseDate.setDateFormatString("yyyy-MM-dd");
        dateReleaseDate.setFont(FONT_INPUT);
        dateReleaseDate.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        dateReleaseDate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        dateReleaseDate.setBackground(WHITE);

        main.add(twoColRow("Language/Type", cmbLanguage, "Release Date (yyyy-MM-dd)", dateReleaseDate));
        main.add(Box.createRigidArea(new Dimension(0, 10)));
        // Status
        cmbStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        cmbStatus.setFont(FONT_INPUT);
        cmbStatus.setBackground(WHITE);
        cmbStatus.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbStatus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        main.add(fieldRow("Status", cmbStatus));
        // Description
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
        main.add(fieldRow("Description", descScroll));
        // Poster URL
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
            JFileChooser chooser = new JFileChooser(new File("src/main/resources/images/movies").getAbsolutePath());
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                String path = f.getAbsolutePath();
                int idx = path.indexOf("images\\movies");
                if (idx != -1)
                    path = path.substring(idx).replace("\\", "/");
                txtPosterUrl.setText(path);
            }
        });

        posterPanel.add(txtPosterUrl, BorderLayout.CENTER);
        posterPanel.add(btnBrowse, BorderLayout.EAST);

        main.add(fieldRow("Poster URL", posterPanel));
        main.add(Box.createRigidArea(new Dimension(0, 8)));
        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(FONT_LABEL);
        lblError.setForeground(PRIMARY_RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(lblError);
        main.add(Box.createRigidArea(new Dimension(0, 8)));
        // ── Buttons ─────────────────────────────────────────────────────
        main.add(buttonRow());
        // wrapper
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
        txtTitle.setText(movie.getTitle());
        txtTitle.setForeground(TEXT_PRIMARY);
        txtGenre.setText(movie.getGenre() != null ? movie.getGenre() : "");
        txtGenre.setForeground(TEXT_PRIMARY);

        spnDuration.setValue(movie.getDurationMins() > 0 ? movie.getDurationMins() : 120);

        if (movie.getFormatMovie() != null) {
            cmbFormat.setSelectedItem(movie.getFormatMovie());
        }

        if (movie.getLanguage() != null) {
            cmbLanguage.setSelectedItem(movie.getLanguage());
        }

        if (movie.getReleaseDate() != null) {
            dateReleaseDate.setDate(java.sql.Date.valueOf(movie.getReleaseDate()));
        }
        cmbStatus.setSelectedItem(movie.getStatus() == MovieStatus.ACTIVE ? "Active" : "Inactive");
        if (movie.getDescription() != null) {
            txtDescription.setText(movie.getDescription());
            txtDescription.setForeground(TEXT_PRIMARY);
        }
        if (movie.getPosterUrl() != null) {
            txtPosterUrl.setText(movie.getPosterUrl());
            txtPosterUrl.setForeground(TEXT_PRIMARY);
        }
    }

    // =====================================================================
    // FIELD BUILDERS
    // =====================================================================
    private JPanel fieldRow(String label, Component input) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        if (input instanceof JTextField) {
            ((JTextField) input).setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            input.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        }
        ((JComponent) input).setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(input);
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        return p;
    }

    private void showGenrePopup(JTextField field) {
        JPopupMenu popup = new JPopupMenu();
        String[] genres = { "Action", "Adventure", "Animation", "Comedy", "Crime", "Drama", "Family", "Fantasy",
                "Horror", "Romance", "Sci-Fi", "Thriller" };
        String current = field.getText();
        for (String g : genres) {
            JCheckBox cb = new JCheckBox(g);
            cb.setSelected(current.contains(g));
            cb.setBackground(WHITE);
            cb.setFont(FONT_INPUT);
            cb.addActionListener(e -> {
                List<String> selected = new ArrayList<>();
                for (Component c : popup.getComponents()) {
                    if (c instanceof JCheckBox && ((JCheckBox) c).isSelected()) {
                        selected.add(((JCheckBox) c).getText());
                    }
                }
                field.setText(String.join(", ", selected));
            });
            popup.add(cb);
        }
        popup.show(field, 0, field.getHeight());
    }

    private JPanel twoColRow(String lbl1, JComponent f1, String lbl2, JComponent f2) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
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
        return row;
    }

    private JPanel buttonRow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        JButton cancelBtn = roundedBtn("Cancel", CANCEL_BG, CANCEL_TEXT, CANCEL_HOVER);
        cancelBtn.addActionListener(e -> dispose());
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        panel.add(cancelBtn, gbc);
        JButton saveBtn = roundedBtn("Update Movie", PRIMARY_RED, WHITE, HOVER_RED);
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
        lblError.setText(" ");
        String title = txtTitle.getText().trim();
        String genre = txtGenre.getText().trim();
        String statusSel = (String) cmbStatus.getSelectedItem();
        String desc = txtDescription.getText().trim();
        String poster = txtPosterUrl.getText().trim();
        // Validation
        if (title.isEmpty()) {
            showError("Vui lòng nhập tên phim.");
            txtTitle.requestFocus();
            return;
        }
        if (genre.isEmpty()) {
            showError("Vui lòng nhập thể loại.");
            txtGenre.requestFocus();
            return;
        }

        int duration = (int) spnDuration.getValue();
        String format = (String) cmbFormat.getSelectedItem();
        String language = (String) cmbLanguage.getSelectedItem();

        LocalDate releaseDate = null;
        if (dateReleaseDate.getDate() != null) {
            releaseDate = dateReleaseDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        // Update model
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDurationMins(duration);
        movie.setFormatMovie(format);
        movie.setLanguage(language.isEmpty() ? null : language);
        movie.setReleaseDate(releaseDate);
        movie.setStatus("Active".equalsIgnoreCase(statusSel) ? MovieStatus.ACTIVE : MovieStatus.INACTIVE);
        movie.setDescription(desc.isEmpty() ? null : desc);
        movie.setPosterUrl(poster.isEmpty() ? null : poster);
        try {
            movieDAO.update(movie);
            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Cập nhật phim thành công!\nMã phim: " + movie.getMovieId(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            showError("Lỗi hệ thống: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String msg) {
        lblError.setText("<html>" + msg + "</html>");
    }

    // =====================================================================
    // INNER
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
