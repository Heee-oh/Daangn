package com.daangn.market.common.event.listener;

import com.daangn.market.appointment.domain.AppointmentStatus;
import com.daangn.market.appointment.infrastructure.AppointmentRepository;
import com.daangn.market.common.event.events.ListingSoldOutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventHandler {

    private final AppointmentRepository appointmentRepository;

    /**
     * 판매 완료 이벤트를 구독하여 해당 거래의 활성 약속을 완료 상태로 정리한다.
     */
    @EventListener
    public void handle(ListingSoldOutEvent event) {
        appointmentRepository.findTopByListingIdAndBuyerIdAndStatusOrderByCreatedAtDesc(
                        event.listingId(),
                        event.buyerId(),
                        AppointmentStatus.SCHEDULED
                )
                .ifPresent(appointment -> {
                    appointment.markDone();
                    appointment.dismissTradePrompt();
                });
    }
}
