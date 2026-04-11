package com.rapphim.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Tiện ích băm và xác minh mật khẩu.
 * <p>
 * Sử dụng SHA-256 + Salt ngẫu nhiên 16 byte.
 * Format lưu DB: {@code <base64-salt>:<base64-hash>}
 * </p>
 *
 * <b>Cách dùng:</b>
 * <pre>
 *   String hash = PasswordUtils.hashPassword("myPassword");
 *   boolean ok  = PasswordUtils.verifyPassword("myPassword", hash);
 * </pre>
 *
 * <b>Lưu ý:</b> Nếu dự án thêm thư viện BCrypt (jBCrypt / Spring Security Crypto)
 * vào pom.xml, nên thay thế class này bằng {@code BCryptPasswordEncoder} để
 * an toàn hơn. SHA-256 + salt đủ dùng cho mục tiêu học tập.
 */
public class PasswordUtils {

    private static final String ALGORITHM  = "SHA-256";
    private static final int    SALT_BYTES = 16;
    private static final String SEPARATOR  = ":";

    private PasswordUtils() {}

    /**
     * Băm mật khẩu với salt ngẫu nhiên.
     *
     * @param plainPassword mật khẩu gốc
     * @return chuỗi {@code salt:hash} dạng Base64 để lưu vào DB
     */
    public static String hashPassword(String plainPassword) {
        byte[] salt = generateSalt();
        byte[] hash = digest(plainPassword, salt);
        return Base64.getEncoder().encodeToString(salt)
                + SEPARATOR
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Xác minh mật khẩu gốc so với hash đã lưu trong DB.
     *
     * @param plainPassword mật khẩu người dùng nhập vào
     * @param storedHash    giá trị {@code password_hash} lưu trong DB
     * @return {@code true} nếu mật khẩu khớp
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        String[] parts = storedHash.split(SEPARATOR, 2);
        if (parts.length != 2) return false;
        try {
            byte[] salt    = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            byte[] actual   = digest(plainPassword, salt);
            return MessageDigest.isEqual(expected, actual);   // constant-time compare
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] digest(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // ── Quick test (dev only) ────────────────────────────────────────────────
    public static void main(String[] args) {
        String h1 = hashPassword("admin123");
        String h2 = hashPassword("staff123");
        System.out.println("admin123 hash : " + h1);
        System.out.println("staff123 hash : " + h2);
        System.out.println("verify admin123: " + verifyPassword("admin123", h1));
        System.out.println("verify wrong   : " + verifyPassword("wrongPass", h1));
    }
}
