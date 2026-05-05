package com.rapphim.view.dialogs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InvoiceDetailDialog extends JDialog {

    public InvoiceDetailDialog(JFrame parent,
                               String invoiceId,
                               List<String[]> details) {
        super(parent, "Invoice Detail - " + invoiceId, true);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JTable table = new JTable(new DefaultTableModel(
                new String[]{"Tên phim", "Mã phim", "Mã vé", "Ghế", "Giá"}, 0
        ));
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        details.forEach(model::addRow);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}