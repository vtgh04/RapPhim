package com.rapphim.service;

import com.rapphim.dao.InvoiceDAO;
import com.rapphim.model.Invoice;
import com.rapphim.model.enums.InvoiceStatus;

import java.sql.Date;
import java.util.List;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    /** Lấy tất cả hóa đơn (không lọc ngày). */
    public List<Invoice> getAllInvoices() throws Exception {
        return invoiceDAO.findAll();
    }

    /** Lấy danh sách hóa đơn theo khoảng ngày. */
    public List<Invoice> getInvoicesByDate(Date from, Date to) throws Exception {
        return invoiceDAO.findByDate(from, to);
    }

    /** Lấy chi tiết hóa đơn (danh sách vé — dùng khi double-click). */
    public List<String[]> getInvoiceDetails(String invoiceId) throws Exception {
        return invoiceDAO.findInvoiceDetails(invoiceId);
    }

    /** Cập nhật trạng thái hóa đơn. */
    public void updateStatus(String invoiceId, InvoiceStatus newStatus) throws Exception {
        invoiceDAO.updateStatus(invoiceId, newStatus);
    }
}
