package com.udacity.pricing.domain.price;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Represents the price of a given vehicle, including currency.
 */
@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long vehicleid;

    private String currency;
    private BigDecimal price;

    public Price(Long vehicleId, String currency, BigDecimal price) {
        this.vehicleid = vehicleId;
        this.currency = currency;
        this.price = price;
    }

    public Price() {}

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getVehicleId() {
        return vehicleid;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleid = vehicleId;
    }
}
