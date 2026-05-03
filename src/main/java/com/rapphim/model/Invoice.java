package com.rapphim.model;

import com.rapphim.model.enums.InvoiceStatus;
import com.rapphim.model.enums.Payment;
import java.time.LocalDateTime;

/**
 * Maps to DB table: dbo.invoices
 * Columns: invoice_id, employee_id, created_at, total_amount, total_tickets,
 * payment_method, status, note
 */
public class Invoice {
    private String invoiceId;
    private String employeeId; // FK → employees
    private String staffName; // joined from employees.full_name
    private LocalDateTime createdAt;
    private double totalAmount; // SUM(final_price) of all tickets
    private int totalTickets; // COUNT of tickets in this invoice
    private Payment paymentMethod;// CASH | CARD | TRANSFER
    private InvoiceStatus status; // PENDING | CONFIRMED | CANCELLED
    private String note;

    public Invoice() {
    }

    /** Full constructor used by DAO. */
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

    // ── Getters ──────────────────────────────────────────────────────────────
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

    // ── Setters ──────────────────────────────────────────────────────────────
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