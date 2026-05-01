package com.rapphim.model.enums;

public enum ShowSeatStatus {
    AVAILABLE("AVAILABLE"),
    HELD("HELD"),
    BOOKED("BOOKED");

    private final String value;

    ShowSeatStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ShowSeatStatus fromString(String text) {
        if (text == null) return AVAILABLE;
        for (ShowSeatStatus s : values()) {
            if (s.value.equalsIgnoreCase(text)) return s;
        }
        return AVAILABLE;
    }
}
