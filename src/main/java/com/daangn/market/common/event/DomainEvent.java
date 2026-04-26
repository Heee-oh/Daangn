package com.daangn.market.common.event;

public interface DomainEvent {

    String aggregateType();

    Long aggregateId();

    default String eventType() {
        return getClass().getSimpleName();
    }
}
