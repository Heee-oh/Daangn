package com.daangn.market.Listing.application.dto;

import com.daangn.market.Listing.domain.HopeLocation;
import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.domain.ListingImage;
import com.daangn.market.Listing.domain.Price;
import com.daangn.market.member.domain.Member;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record ListingDetailResponse(
        Long listingId,
        Long sellerId,
        String sellerNickname,
        String sellerProfileImage,
        int sellerMannerTemp,
        Long buyerId,
        Long reserverId,
        String title,
        String description,
        Long categoryId,
        Long priceAmount,
        boolean isFree,
        String regionName,
        long chatCount,
        boolean hidden,
        String status,
        Integer hopeRegionId,
        BigDecimal hopeLat,
        BigDecimal hopeLng,
        long viewCount,
        List<ListingImageResponse> images,
        Instant createdAt,
        Instant updatedAt
) {
    public static ListingDetailResponse from(
            Listing listing,
            Member seller,
            String regionName,
            long chatCount
    ) {
        Price price = listing.getPrice();
        HopeLocation hopeLocation = listing.getHopeLocation();

        List<ListingImageResponse> imageResponses = listing.getImages().stream()
                .sorted(Comparator.comparingInt(ListingImage::getSortOrder))
                .map(ListingImageResponse::from)
                .toList();

        return new ListingDetailResponse(
                listing.getId(),
                listing.getSellerId(),
                seller.getNickname(),
                seller.getProfileImageUrl(),
                seller.getMannerTemp(),
                listing.getBuyerId(),
                listing.getReserverId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getCategoryId(),
                price == null ? null : price.getPriceAmount(),
                price != null && price.isFree(),
                regionName,
                chatCount,
                listing.isHidden(),
                listing.getStatus().name(),
                hopeLocation == null ? null : hopeLocation.getRegionId(),
                hopeLocation == null ? null : hopeLocation.getLat(),
                hopeLocation == null ? null : hopeLocation.getLng(),
                listing.getViewCount(),
                imageResponses,
                listing.getCreatedAt(),
                listing.getUpdatedAt()
        );
    }
}

