package com.daangn.market.Listing.application;

import com.daangn.market.Listing.application.dto.ListingUpdateCommand;

public interface ListingCommandService {
    Long createDraft(Long sellerId, Integer regionId);

    void update(Long sellerId, Long listingId, ListingUpdateCommand command);

    void publish(Long sellerId, Long listingId);

    void hide(Long sellerId, Long listingId);

    void unhide(Long sellerId, Long listingId);

    void reserve(Long sellerId, Long listingId, Long reserverId);

    void cancelReserve(Long sellerId, Long listingId);

    void markSoldOut(Long sellerId, Long listingId, Long buyerId);

    void remove(Long sellerId, Long listingId);
}
