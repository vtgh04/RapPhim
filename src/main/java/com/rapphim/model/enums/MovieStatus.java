package com.rapphim.model.enums;

public enum MovieStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    MovieStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MovieStatus fromString(String text) {
        for (MovieStatus m : MovieStatus.values()) {
            if (m.value.equalsIgnoreCase(text)) {
                return m;
            }
        }
        return null;
    }
}
