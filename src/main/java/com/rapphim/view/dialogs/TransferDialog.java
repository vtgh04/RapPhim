package com.rapphim.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TransferDialog extends JDialog {

    private boolean isPaid = false;

    // Giả lập thông tin tài khoản Rạp phim
    private static final String BANK_ID = "MB"; // Ngân hàng MB Bank
    private static final String ACCOUNT_NO = "0123456789";
    private static final String ACCOUNT_NAME = "CONG TY RAP PHIM CINEPRO";

    public TransferDialog(Window owner, double amount) {
        super(owner, "Thanh toán Chuyển khoản (VietQR)", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents(amount);
    }

    private void initComponents(double amount) {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 550);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Header
        JLabel lblHeader = new JLabel("Quét mã QR để thanh toán", SwingConstants.CENTER);
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        // QR Image
        JLabel lblQR = new JLabel("Đang tải mã QR...", SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(300, 300));

        try {
            String addInfo = "VeXemPhimCinePro";
            String urlString = String.format(
                    "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%.0f&addInfo=%s&accountName=%s",
                    BANK_ID,
                    ACCOUNT_NO,
                    amount,
                    URLEncoder.encode(addInfo, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8.toString()));

            ImageIcon qrIcon = new ImageIcon(new URL(urlString));
            // Scale if necessary, but VietQR compact2 is usually a good size
            Image img = qrIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            lblQR.setIcon(new ImageIcon(img));
            lblQR.setText(""); // clear text
        } catch (Exception ex) {
            ex.printStackTrace();
            lblQR.setText("Lỗi tải mã QR. Vui lòng kiểm tra mạng!");
        }

        contentPanel.add(lblQR, BorderLayout.CENTER);

        // Info Amount
        JLabel lblAmount = new JLabel(String.format("Số tiền: %,.0f VNĐ", amount), SwingConstants.CENTER);
        lblAmount.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAmount.setForeground(new Color(220, 38, 38)); // Red
        contentPanel.add(lblAmount, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnConfirm = new JButton("Xác nhận đã nhận tiền");
        btnConfirm.setBackground(new Color(220, 38, 38)); // Đỏ đẹp hơn Color.RED
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setOpaque(true);
        btnConfirm.setContentAreaFilled(true);
        btnConfirm.setBorderPainted(false);
        btnConfirm.addActionListener(e -> {
            isPaid = true;
            dispose();
        });

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setBackground(new Color(15, 23, 42)); // Màu giống nút CONFIRM PAYMENT
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.setOpaque(true);
        btnCancel.setContentAreaFilled(true);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> {
            isPaid = false;
            dispose();
        });

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isPaid() {
        return isPaid;
    }
}
