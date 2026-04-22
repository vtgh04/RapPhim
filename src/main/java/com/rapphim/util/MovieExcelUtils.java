package com.rapphim.util;

import com.rapphim.dao.MovieDAO;
import com.rapphim.model.Movie;
import com.rapphim.model.enums.MovieStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MovieExcelUtils {

    /**
     * Xuất danh sách phim ra file Excel.
     */
    public static void exportToExcel(List<Movie> movies, File file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Movies");

            // Tạo header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Row 0: Header
            String[] columns = {"Mã Phim", "Tên Phim", "Thể Loại", "Thời Lượng (Phút)", "Định Dạng", "Phân Loại", "Ngôn Ngữ", "Ngày Phát Hành (dd/MM/yyyy)", "Trạng Thái", "Mô Tả", "Poster URL"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Ghi dữ liệu
            int rowNum = 1;
            for (Movie movie : movies) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(movie.getMovieId() != null ? movie.getMovieId() : "");
                row.createCell(1).setCellValue(movie.getTitle() != null ? movie.getTitle() : "");
                row.createCell(2).setCellValue(movie.getGenre() != null ? movie.getGenre() : "");
                row.createCell(3).setCellValue(movie.getDurationMins());
                row.createCell(4).setCellValue(movie.getFormatMovie() != null ? movie.getFormatMovie() : "");
                row.createCell(5).setCellValue(movie.getRating() != null ? movie.getRating() : "");
                row.createCell(6).setCellValue(movie.getLanguage() != null ? movie.getLanguage() : "");
                row.createCell(7).setCellValue(movie.getReleaseDate() != null ? movie.getReleaseDate().format(dtf) : "");
                row.createCell(8).setCellValue(movie.getStatus() != null ? movie.getStatus().getValue() : "ACTIVE");
                row.createCell(9).setCellValue(movie.getDescription() != null ? movie.getDescription() : "");
                row.createCell(10).setCellValue(movie.getPosterUrl() != null ? movie.getPosterUrl() : "");
            }

            // Tự động chỉnh cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }
        }
    }

    /**
     * Nhập danh sách phim từ file Excel.
     */
    public static List<Movie> importFromExcel(File file, MovieDAO dao) throws Exception {
        List<Movie> importedList = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (FileInputStream in = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();

            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String movieId = getCellValue(row.getCell(0));
                String title = getCellValue(row.getCell(1));
                String genre = getCellValue(row.getCell(2));
                String durationStr = getCellValue(row.getCell(3));
                String formatStr = getCellValue(row.getCell(4));
                String rating = getCellValue(row.getCell(5));
                String language = getCellValue(row.getCell(6));
                String releaseDateStr = getCellValue(row.getCell(7));
                String statusStr = getCellValue(row.getCell(8));
                String description = getCellValue(row.getCell(9));
                String posterUrl = getCellValue(row.getCell(10));

                // Bỏ qua dòng trống
                if (title.isEmpty() && genre.isEmpty()) {
                    continue;
                }

                if (movieId == null || movieId.trim().isEmpty()) {
                    movieId = dao.getNextMovieId();
                }

                int duration = 0;
                try {
                    if (!durationStr.isEmpty()) {
                        duration = Integer.parseInt(durationStr.replaceAll("[^0-9]", ""));
                    }
                } catch (NumberFormatException ignored) {}

                LocalDate releaseDate = null;
                if (!releaseDateStr.isEmpty()) {
                    try {
                        releaseDate = LocalDate.parse(releaseDateStr, dtf);
                    } catch (DateTimeParseException ignored) {}
                }

                MovieStatus status = MovieStatus.fromString(statusStr);
                if (status == null) status = MovieStatus.ACTIVE; // Default

                Movie newMovie = new Movie(movieId, title, genre, duration, formatStr, rating, language, releaseDate, status, description, posterUrl);
                importedList.add(newMovie);
                
                // Nếu sinh mã tự động, cần chèn ngay vào DB để tránh trùng mã ở bản ghi sau
                dao.insert(newMovie);
            }
        }
        return importedList;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                DataFormatter formatter = new DataFormatter();
                return formatter.formatCellValue(cell).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
