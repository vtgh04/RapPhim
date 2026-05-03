package com.rapphim.model.enums;

public enum InvoiceStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED");

    private final String value;

    InvoiceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static InvoiceStatus fromString(String text) {
        if (text == null)
            return PENDING;
        for (InvoiceStatus s : values()) {
            if (s.value.equalsIgnoreCase(text))
                return s;
        }
        return PENDING;
    }
}
