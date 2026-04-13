package com.rapphim.view.panels;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Employee;
import com.rapphim.view.dialogs.AddEmployeeDialog;
import com.rapphim.view.dialogs.EditEmployeeDialog;

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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class EmployeePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 30, 35);
    private static final Color TEXT_SECONDARY = new Color(130, 135, 148);
    private static final Color BORDER_COLOR = new Color(228, 228, 228);
    private static final Color PRIMARY_RED = new Color(220, 38, 38);
    private static final Color HOVER_RED = new Color(185, 28, 28);
    private static final Color GREEN_BG = new Color(209, 250, 229);
    private static final Color GREEN_TEXT = new Color(6, 95, 70);
    private static final Color RED_BG = new Color(254, 226, 226);
    private static final Color RED_TEXT = new Color(185, 28, 28);
    private static final Color MANAGER_BG = new Color(255, 237, 213);
    private static final Color MANAGER_TEXT = new Color(154, 52, 18);
    private static final Color STAFF_BG = new Color(224, 231, 255);
    private static final Color STAFF_TEXT = new Color(55, 48, 163);
    private static final Color TABLE_HEADER_BG = new Color(249, 250, 251);
    private static final Color TABLE_GRID_COLOR = new Color(243, 244, 246);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_SEARCH = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_PILL = new Font("Segoe UI", Font.BOLD, 11);

    // ── Data ─────────────────────────────────────────────────────────────────
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Employee> employees;
    private JComboBox<String> filterCombo;

    // ── Column definition ────────────────────────────────────────────────────
    private static final String[] COLUMNS = {
            "MÃ NV", "TÊN", "USERNAME", "PASSWORD", "ROLE", "STATUS", "PHONE", "EMAIL", "ACTIONS"
    };

    public EmployeePanel() {
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
        loadData();
    }

    // =====================================================================
    // HEADER
    // =====================================================================

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Employees");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage staff access and roles");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        JButton addBtn = createAddButton();
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.add(addBtn);
        header.add(btnWrapper, BorderLayout.EAST);

        return header;
    }

    private JButton createAddButton() {
        JButton btnAdd = new JButton("Add Employee") {
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
        btnAdd.setFont(FONT_BTN);
        btnAdd.setForeground(WHITE);
        btnAdd.setBackground(PRIMARY_RED);
        btnAdd.setOpaque(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(170, 42));

        btnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAdd.setBackground(HOVER_RED);
                btnAdd.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnAdd.setBackground(PRIMARY_RED);
                btnAdd.repaint();
            }
        });

        btnAdd.addActionListener((ActionEvent e) -> handleAddEmployee());
        return btnAdd;
    }

    // =====================================================================
    // SEARCH BAR
    // =====================================================================

    private JPanel createSearchPanel() {
        JPanel searchPanel = new RoundedPanel(14, WHITE);
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(14, BORDER_COLOR),
                new EmptyBorder(10, 18, 10, 18)));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchIcon = new JLabel(loadIcon("images/icons/search.png", 20, 20));
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchIcon.setForeground(TEXT_SECONDARY);

        JTextField searchField = new JTextField();
        searchField.setFont(FONT_SEARCH);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.setOpaque(false);
        searchField.setColumns(30);
        searchField.setText("Search employees...");
        searchField.setForeground(TEXT_SECONDARY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search employees...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search employees...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String text = searchField.getText();
                if (text.equals("Search employees...") || text.isEmpty()) {
                    applyFilters("", filterCombo != null ? (String) filterCombo.getSelectedItem() : "All Roles");
                } else {
                    applyFilters(text, filterCombo != null ? (String) filterCombo.getSelectedItem() : "All Roles");
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
        });

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalGlue());

        JButton filterBtn = new JButton("Filter");
        filterBtn.setFont(FONT_BTN);
        filterBtn.setForeground(TEXT_PRIMARY);
        filterBtn.setBackground(WHITE);
        filterBtn.setOpaque(false);
        filterBtn.setContentAreaFilled(false);
        filterBtn.setFocusPainted(false);
        filterBtn.setBorderPainted(false);
        filterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ImageIcon filterIcon = loadIcon("images/icons/filter.png", 16, 16);
        if (filterIcon != null)
            filterBtn.setIcon(filterIcon);

        searchPanel.add(filterBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        filterCombo = new JComboBox<>(new String[] { "All Roles", "Manager", "Staff" });
        filterCombo.setFont(FONT_TABLE);
        filterCombo.setPreferredSize(new Dimension(120, 30));
        filterCombo.setMaximumSize(new Dimension(120, 30));
        filterCombo.setBackground(WHITE);
        filterCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterCombo.addActionListener(e -> {
            String searchText = searchField.getText();
            if (searchText.equals("Search employees..."))
                searchText = "";
            applyFilters(searchText, (String) filterCombo.getSelectedItem());
        });

        searchPanel.add(filterCombo);
        return searchPanel;
    }

    // =====================================================================
    // TABLE
    // =====================================================================

    private JPanel createTablePanel() {
        JPanel tableContainer = new RoundedPanel(14, WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(14, BORDER_COLOR),
                new EmptyBorder(0, 0, 0, 0)));
        tableContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        table = new JTable(tableModel);
        table.setFont(FONT_TABLE);
        table.setRowHeight(60);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(TABLE_GRID_COLOR);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setBackground(TABLE_HEADER_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TABLE_GRID_COLOR));
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setFont(FONT_HEADER);
        headerRenderer.setForeground(TEXT_SECONDARY);
        headerRenderer.setBackground(TABLE_HEADER_BG);
        headerRenderer.setBorder(new EmptyBorder(0, 0, 0, 0));
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        sorter.setSortable(8, false);

        int[] widths = { 80, 150, 120, 100, 110, 110, 120, 200, 140 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // ── Centered default renderer ───────────────────────────────────
        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setForeground(TEXT_PRIMARY);
                setFont(FONT_TABLE);
                if (!isSelected)
                    setBackground(WHITE);
                return this;
            }
        };
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centeredRenderer);
        }

        // ── PASSWORD column — masked ────────────────────────────────────
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, "••••••••", isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setForeground(TEXT_SECONDARY);
                setFont(FONT_TABLE);
                if (!isSelected)
                    setBackground(WHITE);
                return this;
            }
        });

        // ── ROLE column — Pill JComboBox, no dropdown arrow ─────────────
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            private final JPanel panel = new JPanel(new java.awt.GridBagLayout());
            private final JComboBox<String> combo = new JComboBox<>(new String[] { "MANAGER", "STAFF" });

            {
                panel.setOpaque(true);

                combo.setUI(new BasicComboBoxUI() {
                    @Override
                    protected JButton createArrowButton() {
                        JButton btn = new JButton();
                        btn.setPreferredSize(new Dimension(0, 0));
                        btn.setMaximumSize(new Dimension(0, 0));
                        btn.setBorder(BorderFactory.createEmptyBorder());
                        btn.setVisible(false);
                        return btn;
                    }

                    @Override
                    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(combo.getBackground());
                        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 26, 26);
                        g2.dispose();
                    }
                });

                combo.setFont(FONT_PILL);
                combo.setPreferredSize(new Dimension(100, 26));
                combo.setMinimumSize(new Dimension(100, 26));
                combo.setMaximumSize(new Dimension(100, 26));
                combo.setBorder(BorderFactory.createEmptyBorder());
                combo.setOpaque(false);
                combo.setFocusable(false);

                combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(javax.swing.JList<?> list,
                            Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setHorizontalAlignment(SwingConstants.CENTER);
                        setVerticalAlignment(SwingConstants.CENTER);
                        setFont(FONT_PILL);
                        if (value != null) {
                            if (value.toString().equalsIgnoreCase("MANAGER")) {
                                setBackground(isSelected ? MANAGER_TEXT : MANAGER_BG);
                                setForeground(isSelected ? WHITE : MANAGER_TEXT);
                            } else {
                                setBackground(isSelected ? STAFF_TEXT : STAFF_BG);
                                setForeground(isSelected ? WHITE : STAFF_TEXT);
                            }
                        }
                        setBorder(BorderFactory.createEmptyBorder()); // avoid shift
                        return this;
                    }
                });

                java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
                gbc.anchor = java.awt.GridBagConstraints.CENTER;
                panel.add(combo, gbc);
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                panel.setBackground(isSelected ? t.getSelectionBackground() : WHITE);
                String role = value != null ? value.toString().toUpperCase() : "STAFF";
                combo.setSelectedItem(role);
                if (role.equals("MANAGER")) {
                    combo.setBackground(MANAGER_BG);
                    combo.setForeground(MANAGER_TEXT);
                } else {
                    combo.setBackground(STAFF_BG);
                    combo.setForeground(STAFF_TEXT);
                }
                return panel;
            }
        });

        // ── STATUS column — Pill JComboBox, no dropdown arrow ───────────
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            private final JPanel panel = new JPanel(new java.awt.GridBagLayout());
            private final JComboBox<String> combo = new JComboBox<>(new String[] { "ACTIVE", "RETIRED" });

            {
                panel.setOpaque(true);

                combo.setUI(new BasicComboBoxUI() {
                    @Override
                    protected JButton createArrowButton() {
                        JButton btn = new JButton();
                        btn.setPreferredSize(new Dimension(0, 0));
                        btn.setMaximumSize(new Dimension(0, 0));
                        btn.setBorder(BorderFactory.createEmptyBorder());
                        btn.setVisible(false);
                        return btn;
                    }

                    @Override
                    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(combo.getBackground());
                        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 26, 26);
                        g2.dispose();
                    }
                });

                combo.setFont(FONT_PILL);
                combo.setPreferredSize(new Dimension(100, 26));
                combo.setMinimumSize(new Dimension(100, 26));
                combo.setMaximumSize(new Dimension(100, 26));
                combo.setBorder(BorderFactory.createEmptyBorder());
                combo.setOpaque(false);
                combo.setFocusable(false);

                combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(javax.swing.JList<?> list,
                            Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setHorizontalAlignment(SwingConstants.CENTER);
                        setVerticalAlignment(SwingConstants.CENTER);
                        setFont(FONT_PILL);
                        if (value != null) {
                            if (value.toString().equalsIgnoreCase("ACTIVE")) {
                                setBackground(isSelected ? GREEN_TEXT : GREEN_BG);
                                setForeground(isSelected ? WHITE : GREEN_TEXT);
                            } else {
                                setBackground(isSelected ? RED_TEXT : RED_BG);
                                setForeground(isSelected ? WHITE : RED_TEXT);
                            }
                        }
                        setBorder(BorderFactory.createEmptyBorder()); // avoid shift
                        return this;
                    }
                });

                java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
                gbc.anchor = java.awt.GridBagConstraints.CENTER;
                panel.add(combo, gbc);
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                panel.setBackground(isSelected ? t.getSelectionBackground() : WHITE);
                String status = value != null ? value.toString().toUpperCase() : "ACTIVE";
                combo.setSelectedItem(status);
                if (status.equals("ACTIVE")) {
                    combo.setBackground(GREEN_BG);
                    combo.setForeground(GREEN_TEXT);
                } else {
                    combo.setBackground(RED_BG);
                    combo.setForeground(RED_TEXT);
                }
                return panel;
            }
        });

        // ── ACTIONS column ───────────────────────────────────────────────
        ActionButtonRenderer actionRenderer = new ActionButtonRenderer();
        ActionButtonEditor actionEditor = new ActionButtonEditor();
        table.getColumnModel().getColumn(8).setCellRenderer(actionRenderer);
        table.getColumnModel().getColumn(8).setCellEditor(actionEditor);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        return tableContainer;
    }

    // =====================================================================
    // ACTION BUTTON RENDERER
    // =====================================================================

    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
            setOpaque(true);
            add(createIconButton("images/icons/editing.png"));
            add(createIconButton("images/icons/trash.png"));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            setBackground(isSelected ? t.getSelectionBackground() : WHITE);
            return this;
        }
    }

    // =====================================================================
    // ACTION BUTTON EDITOR
    // =====================================================================

    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton btnEdit;
        private final JButton btnRemove;
        private int currentRow;

        ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            panel.setOpaque(true);
            btnEdit = createIconButton("images/icons/editing.png");
            btnRemove = createIconButton("images/icons/trash.png");

            btnEdit.addActionListener((ActionEvent e) -> {
                int modelRow = table.convertRowIndexToModel(currentRow);
                String employeeId = (String) tableModel.getValueAt(modelRow, 0);
                String fullName = (String) tableModel.getValueAt(modelRow, 1);
                fireEditingStopped();
                handleEditEmployee(employeeId, fullName);
            });

            btnRemove.addActionListener((ActionEvent e) -> {
                int modelRow = table.convertRowIndexToModel(currentRow);
                String employeeId = (String) tableModel.getValueAt(modelRow, 0);
                String fullName = (String) tableModel.getValueAt(modelRow, 1);
                fireEditingStopped();
                handleRemoveEmployee(employeeId, fullName);
            });

            panel.add(btnEdit);
            panel.add(btnRemove);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object value,
                boolean isSelected, int row, int col) {
            currentRow = row;
            panel.setBackground(isSelected ? t.getSelectionBackground() : WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // =====================================================================
    // EVENT HANDLERS
    // =====================================================================

    private void handleAddEmployee() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddEmployeeDialog dialog = new AddEmployeeDialog(parentFrame);
        dialog.setVisible(true);
        if (dialog.isSaved())
            refreshData();
    }

    private void handleEditEmployee(String employeeId, String fullName) {
        try {
            java.util.Optional<Employee> optEmp = employeeDAO.findById(employeeId);
            if (optEmp.isPresent()) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                EditEmployeeDialog dialog = new EditEmployeeDialog(parentFrame, optEmp.get());
                dialog.setVisible(true);
                if (dialog.isSaved())
                    refreshData();
            } else {
                showModernMessageDialog("Lỗi", "Không tìm thấy nhân viên: " + employeeId, true);
            }
        } catch (Exception ex) {
            showModernMessageDialog("Lỗi", "Lỗi khi tải thông tin nhân viên:\n" + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    private void handleRemoveEmployee(String employeeId, String fullName) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        DeleteEmployeeDialog confirmDialog = new DeleteEmployeeDialog(parentFrame, employeeId, fullName);
        confirmDialog.setVisible(true);

        if (confirmDialog.isConfirmed()) {
            try {
                employeeDAO.delete(employeeId);
                showModernMessageDialog("Thành công", "Đã xoá nhân viên: " + fullName, false);
                refreshData();
            } catch (Exception ex) {
                showModernMessageDialog("Lỗi", "Lỗi khi xoá nhân viên:\n" + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    // =====================================================================
    // MODERN DIALOGS
    // =====================================================================

    private class DeleteEmployeeDialog extends JDialog {
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

            JLabel iconLabel = new JLabel(loadIcon("images/icons/trash.png", 22, 22)) {
                {
                    if (getIcon() == null)
                        setText("🗑");
                }
            };
            iconBox.add(iconLabel);

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

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            btnRow.setOpaque(false);
            btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton cancelBtn = makeButton("Huỷ", new Color(243, 244, 246), TEXT_PRIMARY, new Color(229, 231, 235),
                    false);
            cancelBtn.addActionListener(e -> dispose());

            JButton deleteBtn = makeButton("Xoá nhân viên", PRIMARY_RED, WHITE, HOVER_RED, true);
            deleteBtn.addActionListener(e -> {
                confirmed = true;
                dispose();
            });

            btnRow.add(cancelBtn);
            btnRow.add(deleteBtn);

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

        private JPanel makeInfoRow(String label, String value) {
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

        private JPanel makeDivider() {
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

        private JButton makeButton(String text, Color bg, Color fg, Color hover, boolean hasIcon) {
            JButton btn = new JButton(text) {
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
            if (hasIcon) {
                ImageIcon icon = loadIcon("images/icons/trash.png", 14, 14);
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
    }

    private void showModernMessageDialog(String title, String message, boolean isError) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(WHITE);
        Color borderColor = isError ? PRIMARY_RED : GREEN_TEXT;
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, borderColor),
                new EmptyBorder(25, 30, 25, 30)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(borderColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel messageLabel = new JLabel("<html><body>" + message.replace("\n", "<br>") + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color btnBg = isError ? PRIMARY_RED : GREEN_TEXT;
        Color btnHover = isError ? HOVER_RED : new Color(4, 120, 87);
        JButton okBtn = createRoundedButton("OK", btnBg, WHITE, btnHover);
        okBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(okBtn);

        mainPanel.add(btnPanel);
        wrapper.add(mainPanel, BorderLayout.CENTER);

        dialog.setContentPane(wrapper);
        dialog.setVisible(true);
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
        btn.setPreferredSize(new Dimension(100, 38));

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
    // DATA LOADING
    // =====================================================================

    private void loadData() {
        try {
            employees = employeeDAO.findAll();
            populateTable(employees);
        } catch (SQLException e) {
            System.err.println("[EmployeePanel] Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateTable(List<Employee> list) {
        tableModel.setRowCount(0);
        for (Employee emp : list) {
            tableModel.addRow(new Object[] {
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getUsername(),
                    emp.getPassword(),
                    emp.getRole().getValue(),
                    emp.getStatus().getValue(),
                    emp.getPhone(),
                    emp.getEmail(),
                    ""
            });
        }
    }

    public void refreshData() {
        loadData();
    }

    // =====================================================================
    // FILTER LOGIC
    // =====================================================================

    private void applyFilters(String searchText, String roleFilter) {
        List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        if (searchText != null && !searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        }
        if (roleFilter != null && !roleFilter.equals("All Roles")) {
            filters.add(RowFilter.regexFilter("(?i)^" + roleFilter + "$", 4));
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    private JButton createIconButton(String iconPath) {
        JButton btn = new JButton();
        ImageIcon icon = loadIcon(iconPath, 18, 18);
        if (icon != null)
            btn.setIcon(icon);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(32, 32));
        return btn;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null)
            return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // =====================================================================
    // CUSTOM PAINTING HELPERS
    // =====================================================================

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bgColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
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

    // =====================================================================
    // MAIN (test UI độc lập)
    // =====================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employee Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.add(new EmployeePanel());
            frame.setVisible(true);
        });
    }
}