package com.daangn.market.Listing.application.dto;

import com.daangn.market.Listing.domain.ListingImage;

public record ListingImageResponse(
        Long imageId,
        String imageUrl,
        int sortOrder
) {
    public static ListingImageResponse from(ListingImage image) {
        return new ListingImageResponse(
                image.getImageId(),
                image.getImageUrl(),
                image.getSortOrder()
        );
    }
}

