package com.rapphim.model.enums;

public enum CinemaHallStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    CinemaHallStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CinemaHallStatus fromString(String text) {
        if (text == null)
            return null;
        for (CinemaHallStatus status : CinemaHallStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return null;
    }
}
