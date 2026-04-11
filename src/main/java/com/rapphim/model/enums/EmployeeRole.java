package com.rapphim.model.enums;

/**
 * Vai trò nhân viên trong hệ thống rạp chiếu phim.
 * <ul>
 *   <li>MANAGER – nhân viên quản lý → điều hướng vào Admin Page</li>
 *   <li>STAFF   – nhân viên bán vé  → điều hướng vào Staff Page</li>
 * </ul>
 */
public enum EmployeeRole {
    MANAGER("MANAGER"),
    STAFF("STAFF");

    private final String value;

    EmployeeRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /** Chuyển đổi từ chuỗi DB sang enum (case-insensitive). */
    public static EmployeeRole fromString(String text) {
        for (EmployeeRole r : values()) {
            if (r.value.equalsIgnoreCase(text)) return r;
        }
        throw new IllegalArgumentException("Unknown role: " + text);
    }
}
