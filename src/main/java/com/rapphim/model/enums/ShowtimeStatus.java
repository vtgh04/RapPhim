package com.rapphim.model.enums;

public enum ShowtimeStatus {
    SCHEDULED("SCHEDULED"),
    ONGOING("ONGOING"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String value;

    ShowtimeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ShowtimeStatus fromString(String text) {
        if (text == null)
            return SCHEDULED;
        for (ShowtimeStatus s : values()) {
            if (s.value.equalsIgnoreCase(text))
                return s;
        }
        return SCHEDULED;
    }
}
