package com.rapphim.model.enums;

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
            if (r.value.equalsIgnoreCase(text))
                return r;
        }
        throw new IllegalArgumentException("Unknown role: " + text);
    }
}
