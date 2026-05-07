package rapphim.model;

import java.util.Date;

public class Discount {
    private String discountId;
    private String discountName;
    private String discountType;
    private double discountRate;
    private Date validFrom;
    private Date validTo;
    private int minTicketQuantity;
    private boolean isActive;
    private String description;

    public Discount() {
    }

    public Discount(String discountId, String discountName, String discountType, double discountRate, Date validFrom,
            Date validTo, int minTicketQuantity, boolean isActive, String description) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.minTicketQuantity = minTicketQuantity;
        this.isActive = isActive;
        this.description = description;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public int getMinTicketQuantity() {
        return minTicketQuantity;
    }

    public void setMinTicketQuantity(int minTicketQuantity) {
        this.minTicketQuantity = minTicketQuantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
