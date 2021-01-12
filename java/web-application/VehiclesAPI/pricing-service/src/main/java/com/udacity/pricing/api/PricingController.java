package com.udacity.pricing.api;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.service.PriceException;
import com.udacity.pricing.service.PricingService;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implements a REST-based controller for the pricing service.
 */

@RestController
@RequestMapping("services/prices")
public class PricingController {
    private PricingService priceService;

    public PricingController(PricingService pricingService) {
        this.priceService = pricingService;
    }

    /**
     * Gets the price for a requested vehicle.
     * @param vehicleId ID number of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */

    @GetMapping
    public Resource<Price> get(@RequestParam Long vehicleId) {
        return new Resource<>(priceService.getPrice(vehicleId));
    }
}
