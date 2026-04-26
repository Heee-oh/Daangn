package com.daangn.market.Listing.application.dto;

import com.daangn.market.Listing.domain.Status;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.querydsl.core.annotations.QueryProjection;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ListingResponse(
        Long listingId,
        @JsonSerialize(using = ToStringSerializer.class)
        Long sellerId,
        String title,
        Long categoryId,
        Long priceAmount,
        Boolean isFree,
        Status status,
        String dongnm,
        Integer hopeRegionId,
        BigDecimal hopeLat,
        BigDecimal hopeLng,
        Long viewCount,
        Long chatCnt,
        String firstImage,
        Instant updatedAt
) {
    @QueryProjection
    public ListingResponse {
    }


}
