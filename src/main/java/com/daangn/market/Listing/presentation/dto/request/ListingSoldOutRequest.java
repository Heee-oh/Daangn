package com.daangn.market.Listing.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ListingSoldOutRequest(
        @NotNull
        @Positive
        Long buyerId
) {
}

