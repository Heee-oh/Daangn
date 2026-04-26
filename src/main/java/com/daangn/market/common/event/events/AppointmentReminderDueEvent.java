package com.daangn.market.common.event.events;

import com.daangn.market.common.event.DomainEvent;

public record AppointmentReminderDueEvent(
        Long appointmentId,
        Long listingId,
        Long sellerId,
        Long buyerId
) implements DomainEvent {

    @Override
    public String aggregateType() {
        return "APPOINTMENT";
    }

    @Override
    public Long aggregateId() {
        return appointmentId;
    }
}
