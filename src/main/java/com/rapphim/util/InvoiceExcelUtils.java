package com.rapphim.util;

import com.rapphim.model.Invoice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class InvoiceExcelUtils {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat CURRENCY_FMT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private static final String[] HEADERS = {
            "Invoice ID", "Staff", "Created At",
            "Tickets", "Total Amount (VND)", "Payment Method", "Status", "Note"
    };

    public static void export(List<Invoice> invoices, File file) throws Exception {
        if (!file.getName().endsWith(".xlsx"))
            file = new File(file.getAbsolutePath() + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Transactions");

            // ── Header style ────────────────────────────────────────────────
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // ── Base data style ─────────────────────────────────────────────
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // ── Right-aligned style for numbers ─────────────────────────────
            CellStyle numStyle = workbook.createCellStyle();
            numStyle.cloneStyleFrom(dataStyle);
            numStyle.setAlignment(HorizontalAlignment.RIGHT);

            // ── Center style ─────────────────────────────────────────────────
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(dataStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // ── Header row ──────────────────────────────────────────────────
            Row headerRow = sheet.createRow(0);
            headerRow.setHeight((short) 500);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // ── Data rows ───────────────────────────────────────────────────
            int rowIdx = 1;
            for (Invoice inv : invoices) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeight((short) 400);

                setCell(row, 0, inv.getInvoiceId(), centerStyle);
                setCell(row, 1, inv.getStaffName(), dataStyle);
                setCell(row, 2,
                        inv.getCreatedAt() != null ? inv.getCreatedAt().format(DATE_FMT) : "",
                        centerStyle);
                setCell(row, 3, String.valueOf(inv.getTotalTickets()), centerStyle);
                setCell(row, 4,
                        CURRENCY_FMT.format((long) inv.getTotalAmount()) + " ₫",
                        numStyle);
                setCell(row, 5,
                        inv.getPaymentMethod() != null ? inv.getPaymentMethod().getValue() : "",
                        centerStyle);
                setCell(row, 6,
                        inv.getStatus() != null ? inv.getStatus().getValue() : "",
                        centerStyle);
                setCell(row, 7,
                        inv.getNote() != null ? inv.getNote() : "",
                        dataStyle);
            }

            // ── Auto-size columns ───────────────────────────────────────────
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Add small padding after auto-size
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512);
            }

            workbook.write(fos);
        }
    }

    private static void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}
