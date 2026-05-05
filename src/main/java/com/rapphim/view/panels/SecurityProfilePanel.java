package com.rapphim.view.panels;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Employee;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SecurityProfilePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color C_PRIMARY = new Color(17, 24, 39);
    private static final Color C_SECOND = new Color(107, 114, 128);
    private static final Color C_BORDER = new Color(225, 228, 235);
    private static final Color C_ACCENT = new Color(17, 24, 39); // Dark navy button

    private static final Font F_LABEL = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 12);

    private final Employee employee;
    private final Runnable onBack;

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JPasswordField pwdNew;
    private JPasswordField pwdConfirm;

    public SecurityProfilePanel(Employee employee, Runnable onBack) {
        this.employee = employee;
        this.onBack = onBack;
        setBackground(BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));

        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.add(buildEditCard());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        root.add(scroll, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(14, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 24, 0));

        JButton btnBack = new JButton("‹");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnBack.setForeground(C_PRIMARY);
        btnBack.setBackground(WHITE);
        btnBack.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(2, 12, 2, 12)));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> onBack.run());

        JLabel lblTitle = new JLabel("Security & Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(C_PRIMARY);

        bar.add(btnBack, BorderLayout.WEST);
        bar.add(lblTitle, BorderLayout.CENTER);
        return bar;
    }

    // Panel chính để chỉnh sửa
    private JPanel buildEditCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(28, 28, 28, 28)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Phần: Chỉnh sửa thông tin
        JLabel secEdit = new JLabel("Chỉnh sửa thông tin");
        secEdit.setFont(F_SECTION);
        secEdit.setForeground(C_PRIMARY);
        secEdit.setAlignmentX(LEFT_ALIGNMENT);
        card.add(secEdit);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel secSub = new JLabel("Cập nhật thông tin tài khoản");
        secSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        secSub.setForeground(C_SECOND);
        secSub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(secSub);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Dòng 1: Họ tên + Tên đăng nhập
        String nameVal = employee != null && employee.getFullName() != null ? employee.getFullName() : "";
        String userVal = employee != null && employee.getUsername() != null ? employee.getUsername() : "";
        txtFullName = styledField(nameVal, false);
        txtUsername = styledField(userVal, true); // read-only
        card.add(buildFieldRow("Họ và tên", txtFullName, "Username", txtUsername));
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // Dòng 2: Email + Số điện thoại
        String emailVal = employee != null && employee.getEmail() != null ? employee.getEmail() : "";
        String phoneVal = employee != null && employee.getPhone() != null ? employee.getPhone() : "";
        txtEmail = styledField(emailVal, false);
        txtPhone = styledField(phoneVal, false);
        card.add(buildFieldRow("Email", txtEmail, "Số điện thoại", txtPhone));
        card.add(Box.createRigidArea(new Dimension(0, 28)));

        // Đường kẻ ngang
        card.add(buildSep());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Thay đổi mật khẩu
        JLabel secPwd = new JLabel("Thay mật khẩu");
        secPwd.setFont(F_SECTION);
        secPwd.setForeground(C_PRIMARY);
        secPwd.setAlignmentX(LEFT_ALIGNMENT);
        card.add(secPwd);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        pwdNew = styledPwdField();
        pwdConfirm = styledPwdField();
        card.add(buildPwdRow("Mật khẩu mới", pwdNew, "Xác nhận mật khẩu mới", pwdConfirm));
        card.add(Box.createRigidArea(new Dimension(0, 24)));

        JButton btnSave = new JButton("Lưu thay đổi");
        btnSave.setFont(F_BTN);
        btnSave.setBackground(C_ACCENT);
        btnSave.setForeground(WHITE);
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.setOpaque(true);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(180, 42));
        btnSave.setMaximumSize(new Dimension(180, 42));
        btnSave.setAlignmentX(LEFT_ALIGNMENT);
        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSave.setBackground(new Color(55, 65, 81));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSave.setBackground(C_ACCENT);
            }
        });
        btnSave.addActionListener(e -> handleSave());
        card.add(btnSave);

        return card;
    }

    // Dòng chứa 2 cột nhập liệu
    private JPanel buildFieldRow(String lbl1, JComponent f1, String lbl2, JComponent f2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        row.add(fieldBox(lbl1, f1));
        row.add(fieldBox(lbl2, f2));
        return row;
    }

    private JPanel buildPwdRow(String lbl1, JComponent f1, String lbl2, JComponent f2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        row.add(fieldBox(lbl1, f1));
        row.add(fieldBox(lbl2, f2));
        return row;
    }

    private JPanel fieldBox(String label, JComponent field) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_SECOND);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        field.setAlignmentX(LEFT_ALIGNMENT);

        box.add(lbl);
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(field);
        return box;
    }

    // Cấu hình ô nhập liệu
    private JTextField styledField(String value, boolean readOnly) {
        JTextField tf = new JTextField(value);
        tf.setFont(F_INPUT);
        tf.setBackground(readOnly ? new Color(243, 244, 246) : WHITE);
        tf.setForeground(readOnly ? C_SECOND : C_PRIMARY);
        tf.setEditable(!readOnly);
        tf.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return tf;
    }

    private JPasswordField styledPwdField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(F_INPUT);
        pf.setBackground(WHITE);
        pf.setForeground(C_PRIMARY);
        pf.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return pf;
    }

    private JPanel buildSep() {
        JPanel sep = new JPanel();
        sep.setBackground(C_BORDER);
        sep.setOpaque(true);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    // ─ Hàm xử lý khi lưu
    // Regex đồng bộ với AddEmployeeDialog
    private static final String REGEX_FULLNAME = "^[a-zA-Z\\p{L}\\s]{1,25}$";
    private static final String REGEX_PHONE = "^\\d{10}$";
    private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private void handleSave() {
        if (employee == null) {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu: thông tin nhân viên không khả dụng.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newName = txtFullName.getText().trim();
        String newEmail = txtEmail.getText().trim();
        String newPhone = txtPhone.getText().trim();
        String newPwd = new String(pwdNew.getPassword());
        String cfmPwd = new String(pwdConfirm.getPassword());

        // ── Validation (đồng bộ AddEmployeeDialog) ────────────────────────
        if (newName.isEmpty()) {
            warn("Vui lòng nhập họ và tên.");
            txtFullName.requestFocus();
            return;
        }
        if (!newName.matches(REGEX_FULLNAME)) {
            warn("Full name: không chứa số/ký tự đặc biệt, tối đa 25 ký tự.");
            txtFullName.requestFocus();
            return;
        }
        if (!newPhone.isEmpty() && !newPhone.matches(REGEX_PHONE)) {
            warn("Số điện thoại: nhập chính xác 10 chữ số.");
            txtPhone.requestFocus();
            return;
        }
        if (!newEmail.isEmpty() && !newEmail.matches(REGEX_EMAIL)) {
            warn("Email: không đúng định dạng.");
            txtEmail.requestFocus();
            return;
        }
        if (!newPwd.isEmpty()) {
            if (newPwd.length() < 6) {
                warn("Mật khẩu mới phải có ít nhất 6 ký tự.");
                pwdNew.requestFocus();
                return;
            }
            if (!newPwd.equals(cfmPwd)) {
                warn("Xác nhận mật khẩu không khớp.");
                pwdConfirm.requestFocus();
                return;
            }
        }

        // Lưu vào cơ sở dữ liệu
        try {
            Connection conn = DatabaseConnection.getInstance();
            if (!newPwd.isEmpty()) {
                String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ?, password = ? WHERE employee_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setNString(1, newName);
                    ps.setString(2, newEmail.isEmpty() ? null : newEmail);
                    ps.setString(3, newPhone.isEmpty() ? null : newPhone);
                    ps.setString(4, newPwd);
                    ps.setString(5, employee.getEmployeeId());
                    ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ? WHERE employee_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setNString(1, newName);
                    ps.setString(2, newEmail.isEmpty() ? null : newEmail);
                    ps.setString(3, newPhone.isEmpty() ? null : newPhone);
                    ps.setString(4, employee.getEmployeeId());
                    ps.executeUpdate();
                }
            }

            // Cập nhật lại đối tượng nhân viên
            employee.setFullName(newName);
            employee.setEmail(newEmail.isEmpty() ? null : newEmail);
            employee.setPhone(newPhone.isEmpty() ? null : newPhone);

            pwdNew.setText("");
            pwdConfirm.setText("");

            JOptionPane.showMessageDialog(this,
                    "Đã lưu thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            onBack.run();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu thông tin:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
}
