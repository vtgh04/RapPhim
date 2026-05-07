package rapphim.service;

import rapphim.dao.InvoiceDAO;
import rapphim.model.Invoice;
import rapphim.model.enums.InvoiceStatus;

import java.sql.Date;
import java.util.List;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public List<Invoice> getAllInvoices() throws Exception {
        return invoiceDAO.findAll();
    }

    public List<Invoice> getInvoicesByDate(Date from, Date to) throws Exception {
        return invoiceDAO.findByDate(from, to);
    }

    public List<Invoice> getInvoicesByDateAndEmployee(Date from, Date to, String employeeId) throws Exception {
        return invoiceDAO.findByDateAndEmployee(from, to, employeeId);
    }

    public List<String[]> getInvoiceDetails(String invoiceId) throws Exception {
        return invoiceDAO.findInvoiceDetails(invoiceId);
    }

    public void updateStatus(String invoiceId, InvoiceStatus newStatus) throws Exception {
        invoiceDAO.updateStatus(invoiceId, newStatus);
    }
}
