package com.rapphim.model.enums;

/**
 * Trạng thái tài khoản nhân viên.
 * <ul>
 * <li>ACTIVE – tài khoản hoạt động, được phép đăng nhập</li>
 * <li>RETIRED – nhân viên đã nghỉ việc, không được đăng nhập</li>
 * </ul>
 */
public enum EmployeeStatus {
    ACTIVE("ACTIVE"),
    RETIRED("RETIRED");

    private final String value;

    EmployeeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /** Chuyển đổi từ chuỗi DB sang enum (case-insensitive). */
    public static EmployeeStatus fromString(String text) {
        for (EmployeeStatus s : values()) {
            if (s.value.equalsIgnoreCase(text))
                return s;
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}
