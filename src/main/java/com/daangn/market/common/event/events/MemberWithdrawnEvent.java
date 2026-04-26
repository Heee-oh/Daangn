package com.daangn.market.common.event.events;

import com.daangn.market.common.event.DomainEvent;

public record MemberWithdrawnEvent(Long memberId) implements DomainEvent {

    @Override
    public String aggregateType() {
        return "MEMBER";
    }

    @Override
    public Long aggregateId() {
        return memberId;
    }
}
