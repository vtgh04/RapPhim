package com.rapphim.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.rapphim.config.DatabaseConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvoicePdfExporter {

    private static final String FONT_PATH = "C:/Windows/Fonts/arial.ttf"; // Font hỗ trợ tiếng Việt

    public static boolean exportInvoice(String invoiceId) {
        File dir = new File("invoice");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String dest = "invoice/invoice_" + invoiceId + ".pdf";

        String sqlInvoice = "SELECT i.created_at, i.total_amount, i.payment_method, e.full_name " +
                "FROM invoices i " +
                "LEFT JOIN employees e ON i.employee_id = e.employee_id " +
                "WHERE i.invoice_id = ?";

        String sqlTickets = "SELECT ticket_id, final_price FROM tickets WHERE invoice_id = ?";

        try {
            BaseFont bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 22, Font.BOLD);
            Font boldFont = new Font(bf, 12, Font.BOLD);
            Font normalFont = new Font(bf, 12, Font.NORMAL);
            Font italicFont = new Font(bf, 10, Font.ITALIC);

            // Kích thước hóa đơn kiểu máy in nhiệt POS (khoảng 80mm)
            Rectangle pageSize = new Rectangle(250, 600);
            Document document = new Document(pageSize, 15, 15, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(new File(dest)));
            document.open();

            Connection conn = DatabaseConnection.getInstance();

            // 1. Đọc thông tin Hóa đơn
            LocalDateTime createdAt = LocalDateTime.now();
            double totalAmount = 0;
            String paymentMethod = "CASH";
            String employeeName = "Unknown";

            try (PreparedStatement psInv = conn.prepareStatement(sqlInvoice)) {
                psInv.setString(1, invoiceId);
                try (ResultSet rs = psInv.executeQuery()) {
                    if (rs.next()) {
                        createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                        totalAmount = rs.getDouble("total_amount");
                        paymentMethod = rs.getString("payment_method");
                        employeeName = rs.getString("full_name");
                        if (employeeName == null)
                            employeeName = "Unknown";
                    }
                }
            }

            // 2. In Header
            Paragraph pTitle = new Paragraph("HOÁ ĐƠN", titleFont);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            Paragraph pCine = new Paragraph("Rạp phim CinePRO", boldFont);
            pCine.setAlignment(Element.ALIGN_CENTER);
            pCine.setSpacingAfter(10);
            document.add(pCine);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("Date : " + createdAt.format(formatter), normalFont));
            document.add(new Paragraph("Nhân viên xuất vé : " + employeeName, normalFont));
            document.add(new Paragraph(" ", normalFont)); // spacer

            // 3. In bảng vé
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2f, 1.5f, 0.8f, 1.5f }); // Product, Price, Qty, Amount

            // Table Header
            String[] headers = { "Product", "Price", "Qty", "Amount" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, boldFont));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setPaddingBottom(5);
                if (!h.equals("Product"))
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }

            // Table Data
            try (PreparedStatement psTkt = conn.prepareStatement(sqlTickets)) {
                psTkt.setString(1, invoiceId);
                try (ResultSet rs = psTkt.executeQuery()) {
                    while (rs.next()) {
                        String ticketId = rs.getString("ticket_id");
                        double price = rs.getDouble("final_price");

                        // Product
                        PdfPCell cellProduct = new PdfPCell(new Phrase(ticketId, normalFont));
                        cellProduct.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cellProduct);

                        // Price
                        PdfPCell cellPrice = new PdfPCell(new Phrase(String.format("%,.0f", price), normalFont));
                        cellPrice.setBorder(Rectangle.NO_BORDER);
                        cellPrice.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cellPrice);

                        // Qty
                        PdfPCell cellQty = new PdfPCell(new Phrase("1", normalFont));
                        cellQty.setBorder(Rectangle.NO_BORDER);
                        cellQty.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cellQty);

                        // Amount
                        PdfPCell cellAmount = new PdfPCell(new Phrase(String.format("%,.0f", price), normalFont));
                        cellAmount.setBorder(Rectangle.NO_BORDER);
                        cellAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cellAmount);
                    }
                }
            }
            document.add(table);

            // 4. Tổng kết
            document.add(new Paragraph("===============================", normalFont));

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);

            PdfPCell cellTotalLbl = new PdfPCell(new Phrase("Total", boldFont));
            cellTotalLbl.setBorder(Rectangle.NO_BORDER);
            totalTable.addCell(cellTotalLbl);

            PdfPCell cellTotalVal = new PdfPCell(new Phrase(String.format("VND %,.0f", totalAmount), boldFont));
            cellTotalVal.setBorder(Rectangle.NO_BORDER);
            cellTotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(cellTotalVal);

            document.add(totalTable);

            document.add(new Paragraph("--------------------------------------------------", normalFont));

            Paragraph pPayment = new Paragraph("Payment : " + paymentMethod, normalFont);
            document.add(pPayment);

            Paragraph pVat = new Paragraph("(bao gồm 10% vat)", italicFont);
            pVat.setAlignment(Element.ALIGN_RIGHT);
            document.add(pVat);

            document.add(new Paragraph("--------------------------------------------------", normalFont));

            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
