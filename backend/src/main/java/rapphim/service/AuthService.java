package rapphim.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.repository.EmployeeRepository;
import rapphim.model.Employee;
import rapphim.model.dto.RegisterRequest;
import rapphim.model.enums.EmployeeRole;
import rapphim.model.enums.EmployeeStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Service to handle authentication operations (login, registration)
 * with backward compatibility for plain-text passwords and standard BCrypt hashing.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static String loggedInEmployeeId = "EMP001";

    public static String getLoggedInEmployee() {
        return loggedInEmployeeId;
    }

    public static void setLoggedInEmployee(String empId) {
        loggedInEmployeeId = empId;
    }

    public enum AuthError {
        INVALID_CREDENTIALS,
        ACCOUNT_INACTIVE,
        DATABASE_ERROR,
        USERNAME_TAKEN
    }

    public static class AuthException extends Exception {
        private static final long serialVersionUID = 1L;
        private final AuthError error;

        public AuthException(AuthError error, String message) {
            super(message);
            this.error = error;
        }

        public AuthError getError() {
            return error;
        }
    }

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    // Autowired constructor by Spring Boot
    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate an employee. Supports standard BCrypt hashes and fallback plain-text comparison.
     */
    public Employee login(String username, String password) throws AuthException {
        Optional<Employee> opt;
        try {
            opt = employeeRepository.findByUsername(username);
        } catch (Exception e) {
            log.error("[DB ERROR] Lỗi cơ sở dữ liệu khi đăng nhập: {}", e.getMessage(), e);
            throw new AuthException(AuthError.DATABASE_ERROR, e.getMessage());
        }

        if (opt.isEmpty()) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS,
                    "Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        Employee employee = opt.get();

        // Check account status
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new AuthException(AuthError.ACCOUNT_INACTIVE,
                    "Tài khoản đã bị khoá. Vui lòng liên hệ quản lý.");
        }

        // Verify password (supports BCrypt and fallback to plain-text)
        boolean passwordMatches;
        String storedPassword = employee.getPassword();
        if (storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$"))) {
            passwordMatches = passwordEncoder.matches(password, storedPassword);
        } else {
            passwordMatches = password.equals(storedPassword);
        }

        if (!passwordMatches) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS,
                    "Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        // Login successful
        return employee;
    }

    /**
     * Register a new employee with BCrypt hashed password and auto-generated EMP ID.
     */
    @Transactional
    public Employee register(RegisterRequest request) throws AuthException {
        try {
            // Check username duplicate
            boolean usernameExists = employeeRepository.findByUsername(request.getUsername()).isPresent();
                    
            if (usernameExists) {
                throw new AuthException(AuthError.USERNAME_TAKEN, "Tên đăng nhập đã tồn tại.");
            }

            String nextId;
            String maxId = employeeRepository.findMaxEmployeeId();
            if (maxId != null && maxId.startsWith("EMP")) {
                int num = Integer.parseInt(maxId.substring(3));
                nextId = String.format("EMP%03d", num + 1);
            } else {
                nextId = "EMP001";
            }
            
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            EmployeeRole role = EmployeeRole.STAFF;
            if (request.getRole() != null) {
                try {
                    role = EmployeeRole.fromString(request.getRole());
                } catch (IllegalArgumentException ignored) {
                }
            }

            Employee employee = new Employee(
                    nextId,
                    request.getFullName(),
                    request.getUsername(),
                    hashedPassword,
                    role,
                    EmployeeStatus.ACTIVE,
                    request.getPhone(),
                    request.getEmail()
            );

            employeeRepository.save(employee);
            return employee;
        } catch (Exception e) {
            throw new AuthException(AuthError.DATABASE_ERROR, "Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public String getRoleByUsername(String username) throws AuthException {
        Optional<Employee> opt;
        try {
            opt = employeeRepository.findByUsername(username);
        } catch (Exception e) {
            throw new AuthException(AuthError.DATABASE_ERROR, "Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
        return opt.map(emp -> emp.getRole().getValue()).orElse("STAFF");
    }
}
