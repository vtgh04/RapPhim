package com.rapphim.model.enums;

public enum Payment {
    CASH("CASH"),
    CARD("CARD"),
    TRANSFER("TRANSFER");

    private final String value;

    Payment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Payment fromString(String text) {
        if (text == null)
            return CASH;
        for (Payment p : values()) {
            if (p.value.equalsIgnoreCase(text))
                return p;
        }
        return CASH;
    }
}
