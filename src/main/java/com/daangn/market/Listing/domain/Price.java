package com.daangn.market.Listing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Price {

    @Column(name = "price", nullable = false)
    private Long priceAmount;

    @Column(name = "is_free")
    private boolean isFree;

    public Price(Long priceAmount, boolean isFree) {
        this.priceAmount = isFree ? 0 : priceAmount;
        this.isFree = isFree;
    }
}
