package com.rapphim.util;

import com.rapphim.model.Movie;
import com.rapphim.model.Showtime;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ShowtimeExcelUtils {

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public static void exportToExcel(List<Showtime> list, Map<String, Movie> movieCache, File file) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Showtimes");

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header row
            String[] headers = {"ID", "Movie", "Hall", "Start Time", "End Time", "Base Price", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 1;
            for (Showtime st : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(st.getShowtimeId());
                Movie mv = movieCache.get(st.getMovieId());
                row.createCell(1).setCellValue(mv != null ? mv.getTitle() : st.getMovieId());
                row.createCell(2).setCellValue(st.getHallId());
                row.createCell(3).setCellValue(st.getStartTime().format(TF));
                row.createCell(4).setCellValue(st.getEndTime().format(TF));
                row.createCell(5).setCellValue(st.getBasePrice());
                row.createCell(6).setCellValue(st.getStatus().getValue());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
        }
    }
}
