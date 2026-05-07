package com.rapphim.service;

import com.rapphim.dao.EmployeeDAO;
import com.rapphim.model.Employee;
import com.rapphim.util.EmployeeExcelUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    /**
     * Lấy danh sách toàn bộ nhân viên.
     */
    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.findAll();
    }

    /**
     * Tìm nhân viên theo tên đăng nhập (username).
     */
    public Optional<Employee> getEmployeeByUsername(String username) throws SQLException {
        return employeeDAO.findByUsername(username);
    }

    /**
     * Tìm nhân viên theo mã nhân viên.
     */
    public Optional<Employee> getEmployeeById(String id) throws SQLException {
        return employeeDAO.findById(id);
    }

    /**
     * Thêm một nhân viên mới vào hệ thống.
     */
    public void addEmployee(Employee employee) throws SQLException {
        // Có thể bổ sung kiểm tra hợp lệ: ví dụ tên, username không được rỗng
        if (employee.getUsername() == null || employee.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (employee.getPassword() == null || employee.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        employeeDAO.insert(employee);
    }

    /**
     * Cập nhật thông tin nhân viên đã tồn tại.
     */
    public void updateEmployee(Employee employee) throws SQLException {
        if (employee.getUsername() == null || employee.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        employeeDAO.update(employee);
    }

    /**
     * Xóa nhân viên theo mã nhân viên.
     */
    public void deleteEmployee(String employeeId) throws SQLException {
        employeeDAO.delete(employeeId);
    }

    /**
     * Lấy mã nhân viên tiếp theo tự động tăng.
     */
    public String getNextEmployeeId() throws SQLException {
        return employeeDAO.getNextEmployeeId();
    }

    /**
     * Xuất danh sách nhân viên ra file Excel.
     */
    public void exportToExcel(List<Employee> employees, File file) throws Exception {
        EmployeeExcelUtils.exportToExcel(employees, file);
    }

    /**
     * Nhập danh sách nhân viên từ file Excel.
     */
    public List<Employee> importFromExcel(File file) throws Exception {
        return EmployeeExcelUtils.importFromExcel(file, employeeDAO);
    }

    /**
     * Lấy mã nhân viên đang đăng nhập.
     */
    public String getLoggedInEmployee() {
        return AuthService.getLoggedInEmployee();
    }

    /**
     * Thiết lập mã nhân viên đang đăng nhập.
     */
    public void setLoggedInEmployee(String empId) {
        AuthService.setLoggedInEmployee(empId);
    }
}
