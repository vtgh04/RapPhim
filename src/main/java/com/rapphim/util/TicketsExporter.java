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

public class TicketsExporter {

    private static final String FONT_PATH = "C:/Windows/Fonts/arial.ttf"; // Hỗ trợ tiếng Việt

    public static boolean exportTickets(String invoiceId) {
        File dir = new File("invoice/tickets");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String dest = "invoice/tickets/tickets_" + invoiceId + ".pdf";
        String sql = "SELECT mv.title, sh.start_time, sh.end_time, sh.hall_id, " +
                "CONCAT(se.row_char, se.col_number) AS seat_label, " +
                "t.ticket_id, t.final_price, t.barcode " +
                "FROM tickets t " +
                "JOIN show_seats ss ON t.show_seat_id = ss.show_seat_id " +
                "JOIN showtimes sh ON ss.showtime_id = sh.showtime_id " +
                "JOIN movies mv ON sh.movie_id = mv.movie_id " +
                "JOIN seats se ON ss.seat_id = se.seat_id " +
                "WHERE t.invoice_id = ?";

        try {
            // Chuẩn bị Font tiếng Việt
            BaseFont bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 22, Font.BOLD);
            Font subtitleFont = new Font(bf, 14, Font.ITALIC);
            Font normalFont = new Font(bf, 12, Font.NORMAL);
            Font boldFont = new Font(bf, 12, Font.BOLD);
            Font priceFont = new Font(bf, 14, Font.BOLD, BaseColor.RED);

            // Kích thước vé (Tùy chỉnh: 8cm x 15cm tương đương khoảng 226 x 425 point)
            Rectangle pageSize = new Rectangle(250, 400);
            Document document = new Document(pageSize, 15, 15, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(dest)));
            document.open();

            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean firstTicket = true;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                    while (rs.next()) {
                        if (!firstTicket) {
                            document.newPage(); // Mỗi vé một trang
                        }
                        firstTicket = false;

                        String title = rs.getString("title");
                        LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                        LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                        String hallId = rs.getString("hall_id");
                        String seatLabel = rs.getString("seat_label");
                        String ticketId = rs.getString("ticket_id");
                        double price = rs.getDouble("final_price");
                        String barcodeStr = rs.getString("barcode");

                        // 1. Tiêu đề
                        Paragraph pTitle = new Paragraph("VÉ XEM PHIM", titleFont);
                        pTitle.setAlignment(Element.ALIGN_CENTER);
                        document.add(pTitle);

                        Paragraph pSub = new Paragraph("Liên 2 : Khách hàng", subtitleFont);
                        pSub.setAlignment(Element.ALIGN_CENTER);
                        pSub.setSpacingAfter(10);
                        document.add(pSub);

                        // 2. Số vé
                        Paragraph pTicketId = new Paragraph("Số: " + ticketId, normalFont);
                        pTicketId.setAlignment(Element.ALIGN_RIGHT);
                        document.add(pTicketId);

                        // 3. Thông tin rạp
                        Paragraph pCompany = new Paragraph(
                                "CÔNG TY RẠP PHIM CINEPRO\nNguyễn Văn Bảo, Phường Gò Vấp, TP.HCM\nRẠP PHIM CINEPRO",
                                boldFont);
                        pCompany.setAlignment(Element.ALIGN_CENTER);
                        pCompany.setSpacingAfter(10);
                        document.add(pCompany);

                        // 4. Ngày giờ in
                        Paragraph pPrintDate = new Paragraph("Date: " + LocalDateTime.now().format(formatter),
                                normalFont);
                        document.add(pPrintDate);

                        document.add(new Paragraph("--------------------------------------------------", normalFont));

                        // 5. Tên phim & Thời gian
                        Paragraph pMovie = new Paragraph(title, boldFont);
                        pMovie.setSpacingBefore(5);
                        document.add(pMovie);

                        String timeStr = startTime.format(formatter) + " ~ "
                                + endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                        Paragraph pTime = new Paragraph(timeStr, normalFont);
                        pTime.setSpacingAfter(10);
                        document.add(pTime);

                        // 6. Rạp & Ghế & Giá
                        PdfPTable table = new PdfPTable(2);
                        table.setWidthPercentage(100);
                        table.setWidths(new float[] { 1, 1 });

                        PdfPCell cell1 = new PdfPCell(new Phrase("Rạp: " + hallId + "\nGhế: " + seatLabel, boldFont));
                        cell1.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell1);

                        PdfPCell cell2 = new PdfPCell(new Phrase(String.format("%,.0f VNĐ", price), priceFont));
                        cell2.setBorder(Rectangle.NO_BORDER);
                        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell2);

                        document.add(table);

                        // 7. Barcode
                        document.add(new Paragraph(" ")); // Spacer
                        Barcode128 code128 = new Barcode128();
                        code128.setCode(barcodeStr);
                        Image barcodeImage = code128.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK,
                                BaseColor.BLACK);
                        barcodeImage.setAlignment(Element.ALIGN_CENTER);
                        barcodeImage.scalePercent(150);
                        document.add(barcodeImage);
                    }
                }
            }
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
