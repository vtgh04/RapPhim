package rapphim.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rapphim.model.Employee;
import rapphim.model.dto.JwtResponse;
import rapphim.model.dto.LoginRequest;
import rapphim.model.dto.RegisterRequest;
import rapphim.model.dto.RefreshTokenRequest;
import rapphim.security.JwtTokenProvider;
import rapphim.service.AuthService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling user authentication requests (login, registration)
 * exposing endpoints under /api/auth.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Employee employee = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            // Generate Access & Refresh JWTs
            String accessToken = jwtTokenProvider.generateAccessToken(employee.getUsername(), employee.getRole().getValue());
            String refreshToken = jwtTokenProvider.generateRefreshToken(employee.getUsername());

            JwtResponse jwtResponse = new JwtResponse(
                    accessToken,
                    refreshToken,
                    employee.getEmployeeId(),
                    employee.getUsername(),
                    employee.getFullName(),
                    employee.getRole().getValue()
            );

            return ResponseEntity.ok(jwtResponse);
        } catch (AuthService.AuthException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());

            switch (ex.getError()) {
                case INVALID_CREDENTIALS:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
                case ACCOUNT_INACTIVE:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Employee newEmployee = authService.register(registerRequest);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Đăng ký nhân viên mới thành công");
            successResponse.put("employeeId", newEmployee.getEmployeeId());
            successResponse.put("username", newEmployee.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        } catch (AuthService.AuthException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());

            if (ex.getError() == AuthService.AuthError.USERNAME_TAKEN) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromJwt(token);
            try {
                String role = authService.getRoleByUsername(username);
                String newAccessToken = jwtTokenProvider.generateAccessToken(username, role);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
                
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                response.put("refreshToken", newRefreshToken);
                return ResponseEntity.ok(response);
            } catch (AuthService.AuthException ex) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Refresh token không hợp lệ hoặc đã hết hạn.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
