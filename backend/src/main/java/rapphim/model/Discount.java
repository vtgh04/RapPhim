package rapphim.model;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @Column(name = "discount_id")
    private String discountId;

    @Column(name = "discount_name")
    private String discountName;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_rate")
    private double discountRate;

    @Column(name = "valid_from")
    private Date validFrom;

    @Column(name = "valid_to")
    private Date validTo;

    @Column(name = "min_ticket_quantity")
    private int minTicketQuantity;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "description")
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
