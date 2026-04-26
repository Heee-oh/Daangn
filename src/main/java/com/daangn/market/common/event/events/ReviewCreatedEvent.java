package com.daangn.market.common.event.events;

import com.daangn.market.common.event.DomainEvent;

public record ReviewCreatedEvent(
        Long reviewId,
        Long sellerId,
        Long purchaserId,
        boolean isSeller,
        int rating
) implements DomainEvent {

    @Override
    public String aggregateType() {
        return "REVIEW";
    }

    @Override
    public Long aggregateId() {
        return reviewId;
    }
}
