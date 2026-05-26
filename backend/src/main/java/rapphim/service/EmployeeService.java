package rapphim.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.model.Employee;
import rapphim.repository.EmployeeRepository;
import rapphim.util.EmployeeExcelUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public void addEmployee(Employee employee) {
        if (employee.getUsername() == null || employee.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (employee.getPassword() == null || employee.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            employee.setEmployeeId(getNextEmployeeId());
        }
        // Hash password before saving if it is not already a BCrypt hash
        String pwd = employee.getPassword();
        if (pwd != null && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$")) {
            employee.setPassword(passwordEncoder.encode(pwd));
        }
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(Employee employee) {
        if (employee.getUsername() == null || employee.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        // Hash password if updated and not already hashed
        String pwd = employee.getPassword();
        if (pwd != null && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$")) {
            employee.setPassword(passwordEncoder.encode(pwd));
        }
        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(String employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    public String getNextEmployeeId() {
        String maxId = employeeRepository.findMaxEmployeeId();
        if (maxId != null && maxId.startsWith("EMP")) {
            try {
                int num = Integer.parseInt(maxId.substring(3));
                return String.format("EMP%03d", num + 1);
            } catch (NumberFormatException ignored) {}
        }
        return "EMP001";
    }

    public void exportToExcel(List<Employee> employees, File file) throws Exception {
        EmployeeExcelUtils.exportToExcel(employees, file);
    }

    public List<Employee> importFromExcel(File file) throws Exception {
        return EmployeeExcelUtils.importFromExcel(file, this::addEmployee, this::getNextEmployeeId);
    }

    public String getLoggedInEmployee() {
        return AuthService.getLoggedInEmployee();
    }

    public void setLoggedInEmployee(String empId) {
        AuthService.setLoggedInEmployee(empId);
    }
}
