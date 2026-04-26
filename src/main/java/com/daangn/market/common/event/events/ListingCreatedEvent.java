package com.daangn.market.common.event.events;

import com.daangn.market.common.event.DomainEvent;

public record ListingCreatedEvent(
        Long listingId,
        Long sellerId,
        Integer regionId,
        String title
) implements DomainEvent {

    @Override
    public String aggregateType() {
        return "LISTING";
    }

    @Override
    public Long aggregateId() {
        return listingId;
    }
}
