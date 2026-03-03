package com.daangn.market.Listing.presentation.dto.request;

import com.daangn.market.Listing.application.dto.ListingUpdateCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ListingUpdateRequest(
        @NotBlank
        @Size(max = 200)
        String title,
        @NotNull
        String description,
        @NotNull
        Long categoryId,
        @NotNull
        Long priceAmount,
        @NotNull
        Boolean isFree,
        HopeLocationRequest hopeLocation,
        @NotNull
        List<@NotBlank String> imageUrls
) {
    public ListingUpdateCommand toCommand() {

        if (hopeLocation == null) {
            return new ListingUpdateCommand(
                    title,
                    description,
                    categoryId,
                    priceAmount,
                    Boolean.TRUE.equals(isFree),
                    null, null, null,
                    imageUrls
            );
        }

        return new ListingUpdateCommand(
                title,
                description,
                categoryId,
                priceAmount,
                Boolean.TRUE.equals(isFree),
                hopeLocation.regionId(),
                hopeLocation.lat(),
                hopeLocation.lng(),
                imageUrls
        );
    }
}
