package com.rapphim.util;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Employee;
import com.rapphim.model.enums.EmployeeRole;
import com.rapphim.model.enums.EmployeeStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EmployeeExcelUtils {

    /**
     * Xuất danh sách nhân viên ra file Excel.
     */
    public static void exportToExcel(List<Employee> employees, File file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            // Tạo header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Row 0: Header
            String[] columns = {"Mã NV", "Họ Tên", "Tên Đăng Nhập", "Mật Khẩu", "Vai Trò", "Trạng Thái", "SĐT", "Email"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu
            int rowNum = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getEmployeeId() != null ? emp.getEmployeeId() : "");
                row.createCell(1).setCellValue(emp.getFullName() != null ? emp.getFullName() : "");
                row.createCell(2).setCellValue(emp.getUsername() != null ? emp.getUsername() : "");
                row.createCell(3).setCellValue(emp.getPassword() != null ? emp.getPassword() : "");
                row.createCell(4).setCellValue(emp.getRole() != null ? emp.getRole().getValue() : "STAFF");
                row.createCell(5).setCellValue(emp.getStatus() != null ? emp.getStatus().getValue() : "ACTIVE");
                row.createCell(6).setCellValue(emp.getPhone() != null ? emp.getPhone() : "");
                row.createCell(7).setCellValue(emp.getEmail() != null ? emp.getEmail() : "");
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
     * Nhập danh sách nhân viên từ file Excel.
     * Cột dự kiến (0-7): Mã NV, Họ Tên, Tên Đăng Nhập, Mật Khẩu, Vai Trò, Trạng Thái, SĐT, Email
     * Nếu không có Mã NV (hoặc rỗng), tự động sinh Mã NV mới.
     */
    public static List<Employee> importFromExcel(File file, EmployeeDAO dao) throws Exception {
        List<Employee> importedList = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();

            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String empId = getCellValue(row.getCell(0));
                String fullName = getCellValue(row.getCell(1));
                String username = getCellValue(row.getCell(2));
                String password = getCellValue(row.getCell(3));
                String roleStr = getCellValue(row.getCell(4));
                String statusStr = getCellValue(row.getCell(5));
                String phone = getCellValue(row.getCell(6));
                String email = getCellValue(row.getCell(7));

                // Bỏ qua dòng trống
                if (fullName.isEmpty() && username.isEmpty()) {
                    continue;
                }

                if (empId == null || empId.trim().isEmpty()) {
                    empId = dao.getNextEmployeeId();
                }

                EmployeeRole role = EmployeeRole.fromString(roleStr);
                if (role == null) role = EmployeeRole.STAFF; // Default
                EmployeeStatus status = EmployeeStatus.fromString(statusStr);
                if (status == null) status = EmployeeStatus.ACTIVE; // Default

                Employee newEmp = new Employee(empId, fullName, username, password, role, status, phone, email);
                importedList.add(newEmp);
                
                // Nếu sinh mã tự động, cần chèn ngay vào DB để tránh trùng mã ở bản ghi sau
                dao.insert(newEmp);
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
