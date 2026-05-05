package com.rapphim.view.panels;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Invoice;

import javax.swing.*;
import java.sql.Date;
import java.util.List;

/**
 * Panel lịch sử giao dịch dành cho nhân viên (Staff).
 * Kế thừa giao diện từ TransactionPanel.
 * Override loadData() để chỉ hiển thị các hoá đơn do chính nhân viên đang đăng nhập tạo ra.
 */
public class StaffTransactionPanel extends TransactionPanel {

    public StaffTransactionPanel() {
        super();
    }

    @Override
    protected void loadData() {
        String empId = EmployeeDAO.getLoggedInEmployee();

        new SwingWorker<List<Invoice>, Void>() {
            @Override
            protected List<Invoice> doInBackground() throws Exception {
                Date from = new Date(fromDate.getDate().getTime());
                Date to = new Date(toDate.getDate().getTime());
                return invoiceService.getInvoicesByDateAndEmployee(from, to, empId);
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
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(
                            StaffTransactionPanel.this,
                            cause.getMessage(),
                            "Lỗi tải dữ liệu",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
