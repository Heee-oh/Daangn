package com.daangn.market.Listing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListingImage  {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", updatable = false, nullable = false)
    private Listing listing;

    private int sortOrder;
    private String imageUrl;

    protected ListingImage(int sortOrder, String imageUrl) {
        this.sortOrder = sortOrder;
        this.imageUrl = imageUrl;
    }

    public void updateSortOrder(int sortOrder) {
        if (sortOrder < 0) throw new IllegalArgumentException("순서는 음수 불가");
        this.sortOrder = sortOrder;
    }

    public void updateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("잘못된 변경 이미지 요청");
        }

        this.imageUrl = imageUrl;
    }

    public void updateListing(Listing listing) {
        this.listing = listing;
    }


}
