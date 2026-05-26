package rapphim.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rapphim.service.DashboardService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/revenue-by-day")
    public ResponseEntity<List<Object[]>> getRevenueByDay() {
        return ResponseEntity.ok(dashboardService.getRevenueByDay());
    }

    @GetMapping("/top-movies")
    public ResponseEntity<List<Object[]>> getTopMovies() {
        return ResponseEntity.ok(dashboardService.getTopMovies());
    }

    /**
     * Exports a revenue report as an Excel (.xlsx) file.
     * GET /api/dashboard/export/revenue
     */
    @GetMapping("/export/revenue")
    public void exportRevenueReport(HttpServletResponse response) throws IOException {
        String filename = "BaoCaoDoanhThu_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Object[]> revenueByDay = dashboardService.getRevenueByDay();
        List<Object[]> topMovies = dashboardService.getTopMovies();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            // ===== Sheet 1: Revenue by Day =====
            Sheet revenueSheet = workbook.createSheet("Doanh Thu Theo Ngày");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font whiteFont = workbook.createFont();
            whiteFont.setColor(IndexedColors.WHITE.getIndex());
            whiteFont.setBold(true);
            whiteFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(whiteFont);

            // Title row
            Row titleRow = revenueSheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DOANH THU 30 NGÀY GẦN NHẤT");
            titleCell.setCellStyle(headerStyle);
            revenueSheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

            // Column headers
            Row colHeader = revenueSheet.createRow(1);
            colHeader.createCell(0).setCellValue("Ngày (Trong Tháng)");
            colHeader.createCell(1).setCellValue("Doanh Thu (VND)");
            colHeader.getCell(0).setCellStyle(headerStyle);
            colHeader.getCell(1).setCellStyle(headerStyle);

            // Data rows
            CellStyle numberStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("#,##0"));

            int rowIdx = 2;
            double total = 0;
            for (Object[] row : revenueByDay) {
                Row dataRow = revenueSheet.createRow(rowIdx++);
                dataRow.createCell(0).setCellValue(((Number) row[0]).intValue());
                Cell amountCell = dataRow.createCell(1);
                double amount = ((Number) row[1]).doubleValue();
                amountCell.setCellValue(amount);
                amountCell.setCellStyle(numberStyle);
                total += amount;
            }

            // Total row
            Row totalRow = revenueSheet.createRow(rowIdx);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("TỔNG CỘNG");
            totalLabel.setCellStyle(headerStyle);
            Cell totalAmount = totalRow.createCell(1);
            totalAmount.setCellValue(total);
            totalAmount.setCellStyle(numberStyle);

            revenueSheet.autoSizeColumn(0);
            revenueSheet.autoSizeColumn(1);

            // ===== Sheet 2: Top Movies =====
            Sheet moviesSheet = workbook.createSheet("Top Phim Bán Chạy");

            Row movieTitle = moviesSheet.createRow(0);
            Cell movieTitleCell = movieTitle.createCell(0);
            movieTitleCell.setCellValue("TOP 5 PHIM BÁN NHIỀU VÉ NHẤT");
            movieTitleCell.setCellStyle(headerStyle);
            moviesSheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

            Row movieHeader = moviesSheet.createRow(1);
            movieHeader.createCell(0).setCellValue("Tên Phim");
            movieHeader.createCell(1).setCellValue("Số Vé Bán");
            movieHeader.getCell(0).setCellStyle(headerStyle);
            movieHeader.getCell(1).setCellStyle(headerStyle);

            int mIdx = 2;
            for (Object[] row : topMovies) {
                Row mRow = moviesSheet.createRow(mIdx++);
                mRow.createCell(0).setCellValue((String) row[0]);
                mRow.createCell(1).setCellValue(((Number) row[2]).intValue());
            }

            moviesSheet.autoSizeColumn(0);
            moviesSheet.autoSizeColumn(1);

            workbook.write(response.getOutputStream());
        }
    }
}
