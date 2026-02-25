package com.daangn.market.Listing.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ListingUpdateCommand(
        String title,
        String description,
        Long categoryId,
        Long priceAmount,
        boolean isFree,
        Integer hopeRegionId,
        BigDecimal hopeLat,
        BigDecimal hopeLng,
        List<String> imageUrls
) {

}
