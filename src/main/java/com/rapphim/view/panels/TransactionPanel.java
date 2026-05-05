package com.rapphim.view.panels;

import com.rapphim.model.Invoice;
import com.rapphim.model.enums.InvoiceStatus;
import com.rapphim.service.InvoiceService;
import com.rapphim.util.InvoiceExcelUtils;
import com.rapphim.view.dialogs.InvoiceDetailDialog;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TransactionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // Khai báo màu sắc và font chữ
    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_SECONDARY = new Color(130, 135, 148);
    private static final Color BORDER_COLOR = new Color(228, 228, 228);

    private static final Color GREEN_BG = new Color(209, 250, 229);
    private static final Color GREEN_TEXT = new Color(6, 95, 70);
    private static final Color RED_BG = new Color(254, 226, 226);
    private static final Color RED_TEXT = new Color(185, 28, 28);

    private static final Color TABLE_HEADER_BG = new Color(249, 250, 251);
    private static final Color TABLE_GRID_COLOR = new Color(243, 244, 246);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_SEARCH = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_PILL = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    protected DefaultTableModel tableModel;
    protected TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    protected JDateChooser fromDate;
    protected JDateChooser toDate;

    protected final InvoiceService invoiceService = new InvoiceService();
    protected List<Invoice> invoices = new ArrayList<>();

    private static final String[] COLUMNS = {
            "INVOICE ID", "STAFF", "STAFF NAME", "DATE", "TICKETS", "TOTAL", "STATUS", "NOTE"
    };

    public TransactionPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(30, 35, 30, 35));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);
        centerWrapper.add(Box.createRigidArea(new Dimension(0, 20)));
        centerWrapper.add(createSearchPanel());
        centerWrapper.add(Box.createRigidArea(new Dimension(0, 15)));
        centerWrapper.add(createTablePanel());

        add(centerWrapper, BorderLayout.CENTER);

        initDefaultDates();
        loadData();
    }

    // Phần pNorth (Tiêu đề)
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Transactions");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("View ticket sales and invoice history");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        JButton exportBtn = new JButton("Export Excel");
        exportBtn.setFont(FONT_BTN);
        exportBtn.addActionListener(e -> handleExportExcel());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(FONT_BTN);
        refreshBtn.addActionListener(e -> loadData());

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.add(exportBtn);
        btnWrapper.add(refreshBtn);

        header.add(btnWrapper, BorderLayout.EAST);
        return header;
    }

    // Phần pCenter (Tìm kiếm)
    private JPanel createSearchPanel() {
        JPanel panel = new RoundedPanel(14, WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(14, BORDER_COLOR),
                new EmptyBorder(10, 18, 10, 18)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel searchIcon = new JLabel(loadIcon("images/icons/search.png", 20, 20));

        searchField = new JTextField();
        searchField.setFont(FONT_SEARCH);
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.setOpaque(false);

        final String PLACEHOLDER = "Search invoices...";
        searchField.setText(PLACEHOLDER);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(PLACEHOLDER);
                    searchField.setForeground(TEXT_SECONDARY);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!searchField.getText().equals(PLACEHOLDER))
                    applyFilters();
            }

            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

        fromDate = createDateChooser();
        toDate = createDateChooser();

        //
        JLabel lblFrom = new JLabel("From");
        lblFrom.setFont(FONT_TABLE);
        lblFrom.setForeground(TEXT_PRIMARY);

        JLabel lblTo = new JLabel("To");
        lblTo.setFont(FONT_TABLE);
        lblTo.setForeground(TEXT_PRIMARY);

        statusFilter = new JComboBox<>(new String[] { "All Status",
                InvoiceStatus.CONFIRMED.getValue(),
                InvoiceStatus.PENDING.getValue(),
                InvoiceStatus.CANCELLED.getValue() });
        statusFilter.setFont(FONT_TABLE);
        statusFilter.addActionListener(e -> applyFilters());

        panel.add(searchIcon);
        panel.add(searchField);
        panel.add(Box.createHorizontalGlue());
        panel.add(lblFrom);
        panel.add(fromDate);
        panel.add(Box.createHorizontalStrut(6));
        panel.add(lblTo);
        panel.add(toDate);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(statusFilter);

        return panel;
    }

    private JDateChooser createDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setPreferredSize(new Dimension(120, 30));
        dc.setFont(FONT_SEARCH);
        return dc;
    }

    private void initDefaultDates() {
        java.util.Date today = new java.util.Date();
        fromDate.setDate(today);
        toDate.setDate(today);
    }

    // Phần bảng dữ liệu (Table)
    private JPanel createTablePanel() {
        JPanel tableContainer = new RoundedPanel(14, WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new RoundedBorder(14, BORDER_COLOR));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(FONT_TABLE);
        table.setRowHeight(60);
        table.setGridColor(TABLE_GRID_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(TABLE_HEADER_BG);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(5).setCellRenderer(new TotalRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    String invoiceId = table.getValueAt(row, 0).toString();
                    openInvoiceDetail(invoiceId);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scroll, BorderLayout.CENTER);

        return tableContainer;
    }

    // Xử lý logic tải dữ liệu
    protected void loadData() {
        new SwingWorker<List<Invoice>, Void>() {
            @Override
            protected List<Invoice> doInBackground() throws Exception {
                Date from = new Date(fromDate.getDate().getTime());
                Date to = new Date(toDate.getDate().getTime());
                return invoiceService.getInvoicesByDate(from, to);
            }

            @Override
            protected void done() {
                try {
                    invoices = get();
                    tableModel.setRowCount(0);
                    for (Invoice inv : invoices) {
                        tableModel.addRow(new Object[] {
                                inv.getInvoiceId(),
                                inv.getEmployeeId(),
                                inv.getStaffName(),
                                inv.getCreatedAt().toLocalDate().toString(),
                                inv.getTotalTickets(),
                                inv.getTotalAmount(),
                                inv.getStatus().getValue(),
                                inv.getNote() != null ? inv.getNote() : ""
                        });
                    }
                    applyFilters();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(
                            TransactionPanel.this,
                            cause.getMessage(),
                            "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        final String PLACEHOLDER = "Search invoices...";
        String query = searchField.getText();
        if (!query.isEmpty() && !query.equals(PLACEHOLDER)) {
            try {
                filters.add(RowFilter.regexFilter("(?i)" + query));
            } catch (java.util.regex.PatternSyntaxException ignored) {
            }
        }

        if (!"All Status".equals(statusFilter.getSelectedItem())) {
            filters.add(RowFilter.regexFilter("^" + statusFilter.getSelectedItem() + "$", 6));
        }

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void openInvoiceDetail(String invoiceId) {
        try {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            new InvoiceDetailDialog(
                    parent,
                    invoiceId,
                    invoiceService.getInvoiceDetails(invoiceId)).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải chi tiết hóa đơn:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExportExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File("Transactions.xlsx"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                InvoiceExcelUtils.export(invoices, chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Export thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    // Các class hỗ trợ giao diện (Helpers)
    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null)
            return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int r, Color b) {
            radius = r;
            bg = b;
            setOpaque(false);
        }

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
        private final int r;
        private final Color c;

        RoundedBorder(int r, Color c) {
            this.r = r;
            this.c = c;
        }

        public void paintBorder(Component cpn, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(c);
            g2.drawRoundRect(x, y, w - 1, h - 1, r, r);
            g2.dispose();
        }
    }

    private static class TotalRenderer extends DefaultTableCellRenderer {
        private static final java.text.NumberFormat FMT = java.text.NumberFormat
                .getNumberInstance(new java.util.Locale("vi", "VN"));

        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            String text = (v instanceof Number)
                    ? FMT.format(((Number) v).longValue()) + " ₫"
                    : (v != null ? v.toString() : "");
            JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
            lbl.setFont(FONT_TABLE);
            lbl.setForeground(new Color(6, 95, 70));
            lbl.setBorder(new EmptyBorder(0, 8, 0, 12));
            return lbl;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        private static final Color AMBER_BG = new Color(254, 243, 199);
        private static final Color AMBER_TX = new Color(146, 64, 14);

        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            String val = v != null ? v.toString() : "";
            JLabel lbl = new JLabel(val, SwingConstants.CENTER);
            lbl.setFont(FONT_PILL);
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(6, 14, 6, 14));
            switch (val) {
                case "CONFIRMED":
                    lbl.setBackground(GREEN_BG);
                    lbl.setForeground(GREEN_TEXT);
                    break;
                case "PENDING":
                    lbl.setBackground(AMBER_BG);
                    lbl.setForeground(AMBER_TX);
                    break;
                default: // CANCELLED
                    lbl.setBackground(RED_BG);
                    lbl.setForeground(RED_TEXT);
                    break;
            }
            return lbl;
        }
    }
}
