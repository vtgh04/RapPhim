package com.rapphim.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class DeleteEmployeeDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color BG_COLOR = new Color(248, 249, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_SECONDARY = new Color(130, 135, 148);
    private static final Color BORDER_COLOR = new Color(218, 222, 233);
    private static final Color PRIMARY_RED = new Color(220, 38, 38);
    private static final Color HOVER_RED = new Color(185, 28, 28);
    private static final Color RED_BG = new Color(254, 226, 226);
    private static final Color TABLE_HEADER_BG = new Color(249, 250, 251);

    private boolean confirmed = false;

    public DeleteEmployeeDialog(JFrame parent, String employeeId, String fullName) {
        super(parent, "Xoá nhân viên", true);
        setUndecorated(true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_COLOR),
                new EmptyBorder(26, 28, 22, 28)));

        JPanel topRow = new JPanel(new BorderLayout(16, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(44, 44));
        iconBox.setMinimumSize(new Dimension(44, 44));
        iconBox.setMaximumSize(new Dimension(44, 44));
        iconBox.setLayout(new java.awt.GridBagLayout());

        JLabel iconLabel = new JLabel(loadIconStatic("images/icons/trash.png", 22, 22)) {
            {
                if (getIcon() == null)
                    setText("🗑");
            }
        };
        iconBox.add(iconLabel);

        // title + subtitle
        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel title = new JLabel("Xoá nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "<html><body style='width:280px'>Hành động này không thể hoàn tác. ");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);

        titleGroup.add(title);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 3)));
        titleGroup.add(subtitle);

        topRow.add(iconBox, BorderLayout.WEST);
        topRow.add(titleGroup, BorderLayout.CENTER);

        // ── Row 2: employee info card ────────────────────────────────
        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(TABLE_HEADER_BG);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR),
                new EmptyBorder(12, 14, 12, 14)));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        infoCard.add(makeInfoRow("Mã NV", employeeId));
        infoCard.add(Box.createRigidArea(new Dimension(0, 7)));
        infoCard.add(makeDivider());
        infoCard.add(Box.createRigidArea(new Dimension(0, 7)));
        infoCard.add(makeInfoRow("Tên nhân viên", fullName));

        // ── Row 3: buttons ───────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = makeButton("Huỷ",
                new Color(243, 244, 246), TEXT_PRIMARY,
                new Color(229, 231, 235), false);
        cancelBtn.addActionListener(e -> dispose());

        JButton deleteBtn = makeButton("Xoá nhân viên",
                PRIMARY_RED, WHITE, HOVER_RED, true);
        deleteBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(deleteBtn);

        // ── Assemble card ────────────────────────────────────────────
        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(infoCard);
        card.add(Box.createRigidArea(new Dimension(0, 22)));
        card.add(btnRow);

        wrapper.add(card, BorderLayout.CENTER);
        setContentPane(wrapper);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private static JPanel makeInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECONDARY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(TEXT_PRIMARY);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private static JPanel makeDivider() {
        JPanel div = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BORDER_COLOR);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        div.setOpaque(false);
        div.setPreferredSize(new Dimension(0, 1));
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return div;
    }

    private static JButton makeButton(String text, Color bg, Color fg,
            Color hover, boolean hasIcon) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        if (hasIcon) {
            ImageIcon icon = loadIconStatic("images/icons/trash.png", 14, 14);
            if (icon != null)
                btn.setIcon(icon);
            btn.setIconTextGap(6);
        }
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(hasIcon ? 145 : 80, 36));
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

    private static ImageIcon loadIconStatic(String path, int w, int h) {
        URL url = DeleteEmployeeDialog.class.getClassLoader().getResource(path);
        if (url == null)
            return null;
        return new ImageIcon(
                new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
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
