package com.daangn.market.Listing.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record HopeLocationRequest(
        @NotNull Integer regionId,
        @NotNull BigDecimal lat,
        @NotNull BigDecimal lng
) {
}

