package rapphim.model;

import rapphim.model.enums.InvoiceStatus;
import rapphim.model.enums.Payment;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "employee_id")
    private String employeeId;

    @Transient
    private String staffName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "total_tickets")
    private int totalTickets;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private Payment paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status;

    @Column(name = "note")
    private String note;

    public Invoice() {
    }

    public Invoice(String invoiceId, String employeeId, String staffName,
            LocalDateTime createdAt, double totalAmount, int totalTickets,
            Payment paymentMethod, InvoiceStatus status, String note) {
        this.invoiceId = invoiceId;
        this.employeeId = employeeId;
        this.staffName = staffName;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.totalTickets = totalTickets;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.note = note;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getStaffName() {
        return staffName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public Payment getPaymentMethod() {
        return paymentMethod;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public void setPaymentMethod(Payment pm) {
        this.paymentMethod = pm;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Invoice{id=" + invoiceId + ", staff=" + staffName
                + ", tickets=" + totalTickets + ", amount=" + totalAmount
                + ", status=" + status + "}";
    }
}