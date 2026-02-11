package com.daangn.market.Listing.domain;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Price {
    private Long priceAmount;
    private boolean isFree;

    public Price(Long priceAmount, boolean isFree) {
        this.priceAmount = isFree ? 0 : priceAmount;
        this.isFree = isFree;
    }
}
