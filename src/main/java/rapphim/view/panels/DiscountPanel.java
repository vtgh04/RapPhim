package rapphim.view.panels;

import rapphim.model.Discount;
import rapphim.service.DiscountService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DiscountPanel extends JPanel {

    private static final Color BG = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color DARK = new Color(30, 30, 35);
    private static final Color GRAY = new Color(120, 130, 145);
    private static final Color BORDER = new Color(226, 232, 240);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtCode, txtName, txtValue, txtMinOrder, txtSearch, txtValidFrom, txtValidTo;
    private JTextArea txtDesc;
    private JComboBox<String> cbType, cbStatus;

    private DiscountService discountService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DiscountPanel() {
        discountService = new DiscountService();

        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(createHeader(), BorderLayout.NORTH);
        add(createMain(), BorderLayout.CENTER);

        loadDataToTable();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Quản lý mã giảm giá");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(DARK);

        JLabel sub = new JLabel("Thêm, sửa, xóa và quản lý chương trình khuyến mãi.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(GRAY);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(sub);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setBackground(WHITE);
        btnRefresh.setForeground(DARK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            clearForm();
            loadDataToTable();
        });

        panel.add(left, BorderLayout.WEST);
        panel.add(btnRefresh, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMain() {
        JPanel main = new JPanel(new BorderLayout(16, 0));
        main.setOpaque(false);

        main.add(createFormCard(), BorderLayout.WEST);
        main.add(createTableCard(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420, 0));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(24, 24, 24, 24)
        ));
        card.setLayout(new BorderLayout());

        JPanel topWrap = new JPanel();
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));
        topWrap.setOpaque(false);

        JLabel title = new JLabel("Thông tin mã giảm giá");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        topWrap.add(title);
        topWrap.add(Box.createVerticalStrut(20));

        txtCode = createTextField();
        txtName = createTextField();
        txtValue = createTextField();
        txtMinOrder = createTextField();
        txtValidFrom = createTextField();
        txtValidTo = createTextField();
        
        txtValidFrom.setToolTipText("yyyy-MM-dd");
        txtValidTo.setToolTipText("yyyy-MM-dd");

        cbType = new JComboBox<>(new String[]{"PERCENT", "AMOUNT"});
        cbStatus = new JComboBox<>(new String[]{"Hoạt động", "Không hoạt động"});

        txtDesc = new JTextArea(3, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setBorder(new EmptyBorder(8, 10, 8, 10));
        
        JScrollPane descScroll = new JScrollPane(txtDesc);
        descScroll.setBorder(new LineBorder(BORDER, 1, true));

        JPanel formScrollPanel = new JPanel();
        formScrollPanel.setLayout(new BoxLayout(formScrollPanel, BoxLayout.Y_AXIS));
        formScrollPanel.setOpaque(false);

        formScrollPanel.add(createSingleField("Mã giảm giá", txtCode));
        formScrollPanel.add(Box.createVerticalStrut(14));
        formScrollPanel.add(createSingleField("Tên chương trình", txtName));
        formScrollPanel.add(Box.createVerticalStrut(14));
        formScrollPanel.add(createTwoColumnField("Loại giảm", cbType, "Giá trị giảm", txtValue));
        formScrollPanel.add(Box.createVerticalStrut(14));
        formScrollPanel.add(createTwoColumnField("Hiệu lực từ", txtValidFrom, "Đến ngày", txtValidTo));
        formScrollPanel.add(Box.createVerticalStrut(14));
        formScrollPanel.add(createTwoColumnField("Điều kiện (số vé)", txtMinOrder, "Trạng thái", cbStatus));
        formScrollPanel.add(Box.createVerticalStrut(14));
        formScrollPanel.add(createSingleField("Mô tả", descScroll));

        JScrollPane scrollPane = new JScrollPane(formScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        topWrap.add(scrollPane);
        
        card.add(topWrap, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 12, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnAdd = createButton("Thêm", new Color(34, 197, 94));
        JButton btnEdit = createButton("Sửa", new Color(245, 158, 11));
        JButton btnDelete = createButton("Xóa", new Color(239, 68, 68));

        btnAdd.addActionListener(e -> addDiscount());
        btnEdit.addActionListener(e -> editDiscount());
        btnDelete.addActionListener(e -> deleteDiscount());

        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);

        card.add(buttons, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createSingleField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(DARK);
        p.add(l, BorderLayout.NORTH);
        
        if (comp instanceof JScrollPane) {
            comp.setPreferredSize(new Dimension(0, 80));
        } else {
            comp.setPreferredSize(new Dimension(0, 38));
        }
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createTwoColumnField(String label1, JComponent comp1, String label2, JComponent comp2) {
        JPanel wrap = new JPanel(new GridLayout(1, 2, 16, 0));
        wrap.setOpaque(false);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); 

        JPanel p1 = new JPanel(new BorderLayout(0, 6));
        p1.setOpaque(false);
        JLabel l1 = new JLabel(label1);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(DARK);
        p1.add(l1, BorderLayout.NORTH);
        comp1.setPreferredSize(new Dimension(0, 38));
        p1.add(comp1, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout(0, 6));
        p2.setOpaque(false);
        JLabel l2 = new JLabel(label2);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(DARK);
        p2.add(l2, BorderLayout.NORTH);
        comp2.setPreferredSize(new Dimension(0, 38));
        p2.add(comp2, BorderLayout.CENTER);

        wrap.add(p1);
        wrap.add(p2);
        return wrap;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Danh sách mã giảm giá");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(DARK);

        txtSearch = createTextField();
        txtSearch.setPreferredSize(new Dimension(300, 38));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm mã hoặc tên chương trình...");
        txtSearch.addActionListener(e -> searchDiscount());

        top.add(title, BorderLayout.WEST);
        top.add(txtSearch, BorderLayout.EAST);

        String[] cols = {
                "Mã", "Tên chương trình", "Loại", "Giá trị", "Ngày hiệu lực", "Ngày kết thúc", "Điều kiện", "Hoạt động", "Mô tả"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setSelectionBackground(new Color(255, 230, 230));
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(140);

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(WHITE);

        card.add(top, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return txt;
    }

    private void loadDataToTable() {
        try {
            model.setRowCount(0);
            List<Discount> discounts = discountService.getAllDiscounts();
            for (Discount d : discounts) {
                model.addRow(new Object[]{
                        d.getDiscountId(),
                        d.getDiscountName(),
                        d.getDiscountType(),
                        d.getDiscountRate(),
                        d.getValidFrom() != null ? dateFormat.format(d.getValidFrom()) : "",
                        d.getValidTo() != null ? dateFormat.format(d.getValidTo()) : "",
                        d.getMinTicketQuantity(),
                        d.isActive() ? "Hoạt động" : "Không hoạt động",
                        d.getDescription()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private Discount getDiscountFromForm() throws ParseException {
        Discount d = new Discount();
        d.setDiscountId(txtCode.getText().trim());
        d.setDiscountName(txtName.getText().trim());
        d.setDiscountType(cbType.getSelectedItem().toString());
        d.setDiscountRate(txtValue.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtValue.getText().trim()));
        d.setValidFrom(txtValidFrom.getText().trim().isEmpty() ? null : dateFormat.parse(txtValidFrom.getText().trim()));
        d.setValidTo(txtValidTo.getText().trim().isEmpty() ? null : dateFormat.parse(txtValidTo.getText().trim()));
        d.setMinTicketQuantity(txtMinOrder.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtMinOrder.getText().trim()));
        d.setActive(cbStatus.getSelectedIndex() == 0);
        d.setDescription(txtDesc.getText().trim());
        return d;
    }

    private void addDiscount() {
        try {
            Discount d = getDiscountFromForm();
            discountService.addDiscount(d);
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadDataToTable();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + e.getMessage());
        }
    }

    private void editDiscount() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa.");
            return;
        }
        try {
            Discount d = getDiscountFromForm();
            discountService.updateDiscount(d);
            JOptionPane.showMessageDialog(this, "Sửa thành công!");
            loadDataToTable();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + e.getMessage());
        }
    }

    private void deleteDiscount() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String id = model.getValueAt(row, 0).toString();
                discountService.deleteDiscount(id);
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadDataToTable();
                clearForm();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtCode.setText(model.getValueAt(row, 0) != null ? model.getValueAt(row, 0).toString() : "");
        txtName.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
        cbType.setSelectedItem(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "PERCENT");
        txtValue.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
        txtValidFrom.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
        txtValidTo.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
        txtMinOrder.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");
        
        String status = model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "Hoạt động";
        cbStatus.setSelectedIndex(status.equals("Hoạt động") ? 0 : 1);
        
        txtDesc.setText(model.getValueAt(row, 8) != null ? model.getValueAt(row, 8).toString() : "");
    }

    private void clearForm() {
        txtCode.setText("");
        txtName.setText("");
        txtValue.setText("");
        txtMinOrder.setText("");
        txtValidFrom.setText("");
        txtValidTo.setText("");
        txtDesc.setText("");
        cbType.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private void searchDiscount() {
        String keyword = txtSearch.getText().trim();
        try {
            List<Discount> discounts = discountService.searchDiscounts(keyword);
            model.setRowCount(0);
            for (Discount d : discounts) {
                model.addRow(new Object[]{
                        d.getDiscountId(),
                        d.getDiscountName(),
                        d.getDiscountType(),
                        d.getDiscountRate(),
                        d.getValidFrom() != null ? dateFormat.format(d.getValidFrom()) : "",
                        d.getValidTo() != null ? dateFormat.format(d.getValidTo()) : "",
                        d.getMinTicketQuantity(),
                        d.isActive() ? "Hoạt động" : "Không hoạt động",
                        d.getDescription()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);

        btn.setBackground(color);
        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 42));

        return btn;
    }
}