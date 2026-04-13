package com.rapphim.view.dialogs;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeRole;
import com.rapphim.model.enums.EmployeeStatus;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class AddEmployeeDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_COLOR = new Color(248, 249, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_SECONDARY = new Color(130, 135, 148);
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
    private static final Font FONT_HINT = new Font("Segoe UI", Font.ITALIC, 11);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 14);

    // ── Fields ───────────────────────────────────────────────────────────────
    private JTextField txtEmployeeCode;
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JTextField txtStatus;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JLabel lblError;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private boolean saved = false;

    public AddEmployeeDialog(JFrame parent) {
        super(parent, "Add New Employee", true);
        initUI();
        loadNextEmployeeId();
    }

    public boolean isSaved() {
        return saved;
    }

    // =====================================================================
    // UI INITIALIZATION
    // =====================================================================

    private void initUI() {
        setSize(480, 710);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setUndecorated(true);
        getContentPane().setBackground(BG_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_COLOR),
                new EmptyBorder(28, 32, 28, 32)));

        // ── Header ───────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel titleLabel = new JLabel("Add New Employee");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // ── Close button — tự vẽ dấu × bằng Graphics2D ──────────────────
        // Không gọi super.paintComponent → loại bỏ hoàn toàn render L&F
        JButton closeBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int pad = 7;
                int w = getWidth(), h = getHeight();
                g2.drawLine(pad, pad, w - pad, h - pad);
                g2.drawLine(w - pad, pad, pad, h - pad);
                g2.dispose();
            }
        };
        closeBtn.setForeground(CLOSE_NORMAL);
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.setOpaque(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(CLOSE_HOVER);
                closeBtn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(CLOSE_NORMAL);
                closeBtn.repaint();
            }
        });
        headerPanel.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ── Separator ────────────────────────────────────────────────────
        JPanel separator = new JPanel();
        separator.setBackground(BORDER_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(separator);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Form fields ──────────────────────────────────────────────────

        // Employee Code (auto-generated, read-only)
        txtEmployeeCode = createStyledTextField("EMP...");
        txtEmployeeCode.setEditable(false);
        txtEmployeeCode.setBackground(AUTO_BG);
        txtEmployeeCode.setForeground(AUTO_TEXT);
        txtEmployeeCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(createFieldPanel("Employee Code", txtEmployeeCode,
                "Auto-generated based on previous EMP"));

        // Full Name
        txtFullName = createStyledTextField("Enter employee name");
        mainPanel.add(createFieldPanel("Full Name", txtFullName, null));

        // Username
        txtUsername = createStyledTextField("Enter username");
        mainPanel.add(createFieldPanel("Username", txtUsername, null));

        // Password
        txtPassword = createStyledPasswordField("Enter password");
        mainPanel.add(createFieldPanel("Password", txtPassword, null));

        // Role + Status (side by side)
        mainPanel.add(createRoleStatusRow());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // Phone Number
        txtPhone = createStyledTextField("e.g. 0901234567");
        mainPanel.add(createFieldPanel("Phone Number", txtPhone, null));

        // Email Address
        txtEmail = createStyledTextField("employee@cinepro.com");
        mainPanel.add(createFieldPanel("Email Address", txtEmail, null));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        lblError = new JLabel(" ");
        lblError.setFont(FONT_LABEL);
        lblError.setForeground(PRIMARY_RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblError);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // ── Buttons ──────────────────────────────────────────────────────
        mainPanel.add(createButtonPanel());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(mainPanel, BorderLayout.CENTER);

        setContentPane(wrapper);
    }

    // =====================================================================
    // FIELD BUILDERS
    // =====================================================================

    private JPanel createFieldPanel(String labelText, Component inputField, String hintText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, hintText != null ? 90 : 75));

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        if (inputField instanceof JTextField) {
            ((JTextField) inputField).setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            ((JTextField) inputField).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        panel.add(inputField);

        if (hintText != null) {
            panel.add(Box.createRigidArea(new Dimension(0, 4)));
            JLabel hint = new JLabel(hintText);
            hint.setFont(FONT_HINT);
            hint.setForeground(AUTO_TEXT);
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(hint);
        }

        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        return panel;
    }

    private JPanel createRoleStatusRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);

        // Role label + combo
        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
        rolePanel.setOpaque(false);

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(FONT_LABEL);
        roleLabel.setForeground(TEXT_PRIMARY);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolePanel.add(roleLabel);
        rolePanel.add(Box.createRigidArea(new Dimension(0, 6)));

        cmbRole = new JComboBox<>(new String[] { "Staff", "Manager" });
        cmbRole.setFont(FONT_INPUT);
        cmbRole.setPreferredSize(new Dimension(180, 42));
        cmbRole.setBackground(WHITE);
        cmbRole.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cmbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolePanel.add(cmbRole);

        gbc.gridx = 0;
        gbc.weightx = 0.5;
        row.add(rolePanel, gbc);

        // Status label + fixed text field
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(FONT_LABEL);
        statusLabel.setForeground(TEXT_PRIMARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        txtStatus = createStyledTextField("");
        txtStatus.setText("Active");
        txtStatus.setEditable(false);
        txtStatus.setBackground(WHITE);
        txtStatus.setForeground(TEXT_SECONDARY);
        txtStatus.setFont(FONT_INPUT);
        txtStatus.setPreferredSize(new Dimension(160, 42));
        txtStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.add(txtStatus);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(statusPanel, gbc);

        return row;
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        JButton cancelBtn = createRoundedButton("Cancel", CANCEL_BG, CANCEL_TEXT, CANCEL_HOVER);
        cancelBtn.addActionListener(e -> dispose());
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        btnPanel.add(cancelBtn, gbc);

        JButton saveBtn = createRoundedButton("Save Employee", PRIMARY_RED, WHITE, HOVER_RED);
        saveBtn.addActionListener(e -> handleSave());
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 0, 0, 0);
        btnPanel.add(saveBtn, gbc);

        return btnPanel;
    }

    // =====================================================================
    // INPUT COMPONENT FACTORY
    // =====================================================================

    private JTextField createStyledTextField(String placeholder) {
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

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_HINT);
                }
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
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
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBackground(WHITE);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                    field.setEchoChar('●');
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(TEXT_HINT);
                }
            }
        });

        return field;
    }

    private JButton createRoundedButton(String text, Color bg, Color fg, Color hoverBg) {
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
                btn.setBackground(hoverBg);
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

    private void loadNextEmployeeId() {
        try {
            String nextId = employeeDAO.getNextEmployeeId();
            txtEmployeeCode.setText(nextId);
        } catch (Exception ex) {
            txtEmployeeCode.setText("EMP???");
            System.err.println("[AddEmployeeDialog] Lỗi lấy mã NV: " + ex.getMessage());
        }
    }

    private void handleSave() {
        lblError.setText(" "); // clear errors

        String empId = txtEmployeeCode.getText().trim();
        String fullName = getFieldValue(txtFullName, "Enter employee name");
        String username = getFieldValue(txtUsername, "Enter username");
        String password = String.valueOf(txtPassword.getPassword());
        if (password.equals("Enter password"))
            password = "";
        String phone = getFieldValue(txtPhone, "e.g. 0901234567");
        String email = getFieldValue(txtEmail, "employee@cinepro.com");
        String roleStr = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty()) {
            showError("Vui lòng nhập họ tên nhân viên.");
            txtFullName.requestFocus();
            return;
        }
        if (!fullName.matches("^[a-zA-Z\\p{L}\\s]{1,25}$")) {
            showError("Full name: không chứa số/kí tự đặc biệt, tối đa 25 kí tự.");
            txtFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập.");
            txtUsername.requestFocus();
            return;
        }
        if (!username.matches("^[a-zA-Z0-9]{6,20}$")) {
            showError("Username: chữ và số, độ dài từ 6 - 20 kí tự.");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu.");
            txtPassword.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^\\d{10}$")) {
            showError("Phone Number: bắt buộc nhập chính xác 10 số.");
            txtPhone.requestFocus();
            return;
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showError("Email: không đúng định dạng.");
            txtEmail.requestFocus();
            return;
        }

        try {
            for (Employee e : employeeDAO.findAll()) {
                if (e.getUsername().equalsIgnoreCase(username)) {
                    showError("Username đã tồn tại trong hệ thống.");
                    txtUsername.requestFocus();
                    return;
                }
                if (!email.isEmpty() && email.equalsIgnoreCase(e.getEmail())) {
                    showError("Email đã tồn tại trong hệ thống.");
                    txtEmail.requestFocus();
                    return;
                }
            }
        } catch (Exception ex) {
            System.err.println("Database check error: " + ex.getMessage());
        }

        EmployeeRole role = roleStr.equalsIgnoreCase("Manager")
                ? EmployeeRole.MANAGER
                : EmployeeRole.STAFF;

        Employee employee = new Employee(
                empId, fullName, username, password,
                role, EmployeeStatus.ACTIVE,
                phone.isEmpty() ? null : phone,
                email.isEmpty() ? null : email);

        try {
            employeeDAO.insert(employee);
            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Thêm nhân viên thành công!\nMã NV: " + empId,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            showError("Lỗi hệ thống: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String val = field.getText().trim();
        return val.equals(placeholder) ? "" : val;
    }

    private void showError(String msg) {
        lblError.setText("<html>" + msg + "</html>");
    }

    // =====================================================================
    // CUSTOM PAINTING HELPERS
    // =====================================================================

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