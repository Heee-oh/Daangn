package com.daangn.market.common.event.listener;

import com.daangn.market.common.event.events.AppointmentReminderDueEvent;
import com.daangn.market.common.event.events.AppointmentTradePromptEvent;
import com.daangn.market.common.event.events.ChatMessageSentEvent;
import com.daangn.market.common.event.events.ChatStartedEvent;
import com.daangn.market.common.event.events.ListingCreatedEvent;
import com.daangn.market.common.event.events.ListingReservationCanceledEvent;
import com.daangn.market.common.event.events.ListingSoldOutEvent;
import com.daangn.market.notification.domain.KeywordSubscription;
import com.daangn.market.notification.domain.Notification;
import com.daangn.market.notification.domain.NotificationType;
import com.daangn.market.notification.infrastructure.KeywordSubscriptionRepository;
import com.daangn.market.notification.infrastructure.NotificationRepository;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationRepository notificationRepository;
    private final KeywordSubscriptionRepository keywordSubscriptionRepository;

    /**
     * 예약 취소 이벤트를 구독하여 기존 예약자에게 알림을 생성한다.
     */
    @EventListener
    public void handle(ListingReservationCanceledEvent event) {
        if (event.reserverId() == null) {
            return;
        }
        notificationRepository.save(Notification.create(event.reserverId(), NotificationType.RESERVATION_CANCELED));
    }

    /**
     * 판매 완료 이벤트를 구독하여 판매자와 구매자에게 알림을 생성한다.
     */
    @EventListener
    public void handle(ListingSoldOutEvent event) {
        notificationRepository.save(Notification.create(event.sellerId(), NotificationType.SOLD_OUT));
        notificationRepository.save(Notification.create(event.buyerId(), NotificationType.SOLD_OUT));
        // 거래 완료 후 구매자에게 리뷰 작성을 유도하는 알림을 추가로 보낸다.
        notificationRepository.save(Notification.create(event.buyerId(), NotificationType.REVIEW_REQUEST));
    }

    /**
     * 채팅방 생성 이벤트를 구독하여 판매자에게 새 채팅 알림을 생성한다.
     */
    @EventListener
    public void handle(ChatStartedEvent event) {
        notificationRepository.save(Notification.create(event.sellerId(), NotificationType.NEW_CHAT));
    }

    /**
     * 채팅 메시지 전송 이벤트를 구독하여 상대방에게 알림을 생성한다.
     */
    @EventListener
    public void handle(ChatMessageSentEvent event) {
        notificationRepository.save(Notification.create(event.otherMemberId(), NotificationType.NEW_CHAT));
    }

    /**
     * 약속 생성 이벤트를 구독하여 약속 참여자에게 알림을 생성한다.
     */
    @EventListener
    public void handle(AppointmentReminderDueEvent event) {
        notificationRepository.save(Notification.create(event.sellerId(), NotificationType.APPOINTMENT_ALARM));
        notificationRepository.save(Notification.create(event.buyerId(), NotificationType.APPOINTMENT_ALARM));
    }

    /**
     * 거래 완료 유도 이벤트를 구독하여 판매자에게 인앱 팝업용 알림을 생성한다.
     */
    @EventListener
    public void handle(AppointmentTradePromptEvent event) {
        notificationRepository.save(Notification.create(event.sellerId(), NotificationType.TRADE_COMPLETE_PROMPT));
    }

    /**
     * 게시글 등록 이벤트를 구독하여 키워드 구독자에게 알림을 생성한다.
     */
    @EventListener
    public void handle(ListingCreatedEvent event) {
        if (event.title() == null || event.title().isBlank()) {
            return;
        }

        String normalizedTitle = event.title().toLowerCase(Locale.ROOT);
        Set<Long> notifiedMemberIds = new HashSet<>();
        for (KeywordSubscription subscription : keywordSubscriptionRepository.findByRegionIdOrRegionIdIsNull(event.regionId())) {
            if (normalizedTitle.contains(subscription.getKeyword().toLowerCase(Locale.ROOT))
                    && notifiedMemberIds.add(subscription.getMemberId())) {
                notificationRepository.save(Notification.create(subscription.getMemberId(), NotificationType.LISTING_KEYWORD));
            }
        }
    }
}
