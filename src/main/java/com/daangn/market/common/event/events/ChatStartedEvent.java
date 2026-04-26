package com.daangn.market.common.event.events;

import com.daangn.market.common.event.DomainEvent;

public record ChatStartedEvent(
        Long chatRoomId,
        Long listingId,
        Long buyerId,
        Long sellerId
) implements DomainEvent {

    @Override
    public String aggregateType() {
        return "CHAT_ROOM";
    }

    @Override
    public Long aggregateId() {
        return chatRoomId;
    }
}
