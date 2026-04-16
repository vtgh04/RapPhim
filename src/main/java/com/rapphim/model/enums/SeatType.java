package com.rapphim.model.enums;

public enum SeatType {
    REGULAR("REGULAR"),
    VIP("VIP");

    private final String value;

    SeatType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SeatType fromString(String text) {
        if (text == null) return null;
        for (SeatType type : SeatType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
