package com.rapphim.view.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PaymentDialog extends JDialog {

    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TXT_DARK = new Color(15, 23, 42);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color PRIMARY = new Color(220, 38, 38);

    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_NORM = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_H1 = new Font("Segoe UI", Font.BOLD, 20);

    private String selectedMethod = null;

    public PaymentDialog(Window owner, double totalAmount) {
        super(owner, "Payment", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // Draw a subtle border
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Payment");
        lblTitle.setFont(F_H1.deriveFont(22f));
        lblTitle.setForeground(TXT_DARK);

        JButton btnClose = new JButton("X");
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFont(F_NORM.deriveFont(16f));
        btnClose.setForeground(MUTED);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Không thành công");
            selectedMethod = null;
            dispose();
        });

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(24, 0, 24, 0));

        JLabel lblMethodTitle = new JLabel("SELECT PAYMENT METHOD");
        lblMethodTitle.setFont(F_BOLD.deriveFont(10f));
        lblMethodTitle.setForeground(MUTED);
        lblMethodTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblMethodTitle);
        body.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        class PaymentCard extends JPanel {
            boolean isSelected = false;
            String method;
            JLabel lblText;

            public PaymentCard(String method, String title, String iconPath) {
                this.method = method;
                setLayout(new BorderLayout());
                setOpaque(false);
                setPreferredSize(new Dimension(100, 90));
                setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel lblIcon = new JLabel();
                lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
                try {
                    java.net.URL url = getClass().getResource(iconPath);
                    if (url != null) {
                        Image img = new ImageIcon(url).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                        lblIcon.setIcon(new ImageIcon(img));
                    }
                } catch (Exception ex) {
                }

                lblText = new JLabel(title);
                lblText.setFont(F_BOLD.deriveFont(10f));
                lblText.setHorizontalAlignment(SwingConstants.CENTER);
                lblText.setForeground(MUTED);

                add(lblIcon, BorderLayout.CENTER);
                add(lblText, BorderLayout.SOUTH);
                setBorder(new EmptyBorder(16, 4, 16, 4));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(new Color(254, 242, 242));
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2.setColor(PRIMARY);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2.setColor(BORDER);
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }

            public void setSelected(boolean b) {
                this.isSelected = b;
                lblText.setForeground(b ? PRIMARY : MUTED);
                repaint();
            }
        }

        PaymentCard cardCredit = new PaymentCard("CARD", "CREDIT CARD", "/images/icons/credit-card.png");
        PaymentCard cardCash = new PaymentCard("CASH", "CASH", "/images/icons/money.png");
        PaymentCard cardBank = new PaymentCard("TRANSFER", "BANK", "/images/icons/transfer.png");

        List<PaymentCard> allCards = Arrays.asList(cardCredit, cardCash, cardBank);

        for (PaymentCard c : allCards) {
            cardsPanel.add(c);
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedMethod = c.method;
                    for (PaymentCard oc : allCards) {
                        oc.setSelected(oc == c);
                    }
                }
            });
        }

        body.add(cardsPanel);
        body.add(Box.createRigidArea(new Dimension(0, 24)));

        // Summary box
        JPanel summaryBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        summaryBox.setOpaque(false);
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.Y_AXIS));
        summaryBox.setBorder(new EmptyBorder(20, 20, 20, 20));
        summaryBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        JLabel lblTotalTitle = new JLabel("TOTAL PAYABLE");
        lblTotalTitle.setFont(F_BOLD.deriveFont(14f));
        lblTotalTitle.setForeground(TXT_DARK);
        JLabel valTotal = new JLabel(String.format("%,.0f đ", totalAmount));
        valTotal.setFont(F_BOLD.deriveFont(18f));
        valTotal.setForeground(PRIMARY);
        row2.add(lblTotalTitle, BorderLayout.WEST);
        row2.add(valTotal, BorderLayout.EAST);

        summaryBox.add(row2);

        body.add(summaryBox);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton btnConfirm = new JButton("CONFIRM PAYMENT");
        btnConfirm.setBackground(TXT_DARK);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(F_BOLD.deriveFont(13f));
        btnConfirm.setOpaque(true);
        btnConfirm.setContentAreaFilled(true);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(0, 48));
        btnConfirm.addActionListener(e -> {
            if (selectedMethod == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán!");
                return;
            }
            dispose();
        });
        footer.add(btnConfirm, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(body, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setSize(new Dimension(380, 420));
        if (owner != null)
            setLocationRelativeTo(owner);
    }

    public String getSelectedMethod() {
        return selectedMethod;
    }
}
