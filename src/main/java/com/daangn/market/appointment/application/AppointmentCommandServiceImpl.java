package com.daangn.market.appointment.application;

import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.domain.Status;
import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import com.daangn.market.appointment.application.dto.AppointmentResponse;
import com.daangn.market.appointment.application.dto.TradePromptResponse;
import com.daangn.market.appointment.domain.Appointment;
import com.daangn.market.appointment.domain.AppointmentStatus;
import com.daangn.market.appointment.infrastructure.AppointmentRepository;
import com.daangn.market.chat.domain.ChatRoom;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.infrastructure.ChatRoomRepository;
import com.daangn.market.common.event.DomainEventPublisher;
import com.daangn.market.common.event.events.AppointmentReminderDueEvent;
import com.daangn.market.common.event.events.AppointmentScheduledEvent;
import com.daangn.market.common.event.events.AppointmentTradePromptEvent;
import com.daangn.market.member.domain.Member;
import com.daangn.market.member.infrastructure.member.MemberJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentCommandServiceImpl implements AppointmentCommandService {

    private final AppointmentRepository appointmentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ListingJpaRepository listingJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * 채팅방 참여자가 약속을 생성하고 현재 활성 약속으로 저장한다.
     */
    @Override
    @Transactional
    public AppointmentResponse schedule(
            Long memberId,
            Long chatRoomId,
            Instant meetAt,
            Integer reminderMinutes
    ) {
        ChatRoom chatRoom = findParticipatingChatRoom(memberId, chatRoomId);

        appointmentRepository.findTopByListingIdAndBuyerIdAndStatusOrderByCreatedAtDesc(
                        chatRoom.getListingId(),
                        chatRoom.getBuyerId(),
                        AppointmentStatus.SCHEDULED
                )
                .ifPresent(Appointment::cancel);

        Appointment appointment = Appointment.schedule(
                chatRoom.getListingId(),
                chatRoom.getSellerId(),
                chatRoom.getBuyerId(),
                meetAt,
                reminderMinutes
        );
        Appointment saved = appointmentRepository.save(appointment);

        domainEventPublisher.publish(new AppointmentScheduledEvent(
                saved.getId(),
                saved.getListingId(),
                saved.getSellerId(),
                saved.getBuyerId(),
                saved.getNotificationTime()
        ));

        return new AppointmentResponse(saved.getId(), saved.getMeetAt(), saved.getReminderMinutes());
    }

    /**
     * 활성 약속을 취소하고 더 이상 예약 흐름에 사용하지 않는다.
     */
    @Override
    @Transactional
    public void cancel(Long memberId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdAndStatus(appointmentId, AppointmentStatus.SCHEDULED)
                .orElseThrow(EntityNotFoundException::new);
        validateParticipant(appointment, memberId);
        appointment.cancel();
    }

    /**
     * 판매자에게 보여줄 거래 완료 유도 팝업 대상을 조회한다.
     */
    @Override
    public Optional<TradePromptResponse> getTradePrompt(Long memberId) {
        return appointmentRepository
                .findBySellerIdAndStatusAndTradePromptSentAtIsNotNullAndTradePromptDismissedAtIsNullOrderByTradePromptSentAtDesc(
                        memberId,
                        AppointmentStatus.SCHEDULED
                )
                .stream()
                .filter(this::isTradePromptEligible)
                .findFirst()
                .map(this::toTradePromptResponse);
    }

    /**
     * 판매자가 이번 거래 완료 유도 팝업을 닫으면 다시 노출하지 않는다.
     */
    @Override
    @Transactional
    public void dismissTradePrompt(Long memberId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdAndStatus(appointmentId, AppointmentStatus.SCHEDULED)
                .orElseThrow(EntityNotFoundException::new);
        if (!appointment.getSellerId().equals(memberId)) {
            throw new EntityNotFoundException();
        }
        appointment.dismissTradePrompt();
    }

    /**
     * 도래한 약속 알림과 거래 완료 유도 시점을 서버에서 처리한다.
     */
    @Override
    @Transactional
    public void processDueNotifications() {
        Instant now = Instant.now();

        for (Appointment appointment : appointmentRepository
                .findByStatusAndNotificationTimeLessThanEqualAndReminderSentAtIsNull(AppointmentStatus.SCHEDULED, now)) {
            appointment.markReminderSent();
            domainEventPublisher.publish(new AppointmentReminderDueEvent(
                    appointment.getId(),
                    appointment.getListingId(),
                    appointment.getSellerId(),
                    appointment.getBuyerId()
            ));
        }

        Instant promptThreshold = now.minus(Duration.ofMinutes(3));
        for (Appointment appointment : appointmentRepository
                .findByStatusAndTradePromptSentAtIsNullAndMeetAtLessThanEqual(AppointmentStatus.SCHEDULED, promptThreshold)) {
            if (!isTradePromptEligible(appointment)) {
                continue;
            }

            appointment.markTradePromptSent();
            domainEventPublisher.publish(new AppointmentTradePromptEvent(
                    appointment.getId(),
                    appointment.getListingId(),
                    appointment.getSellerId(),
                    appointment.getBuyerId()
            ));
        }
    }

    private ChatRoom findParticipatingChatRoom(Long memberId, Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .filter(room -> room.getStatus() == ChatRoomStatus.ACTIVE)
                .filter(room -> room.isParticipant(memberId))
                .orElseThrow(EntityNotFoundException::new);
    }

    private void validateParticipant(Appointment appointment, Long memberId) {
        if (!appointment.getSellerId().equals(memberId) && !appointment.getBuyerId().equals(memberId)) {
            throw new EntityNotFoundException();
        }
    }

    private boolean isTradePromptEligible(Appointment appointment) {
        Listing listing = listingJpaRepository.findByIdAndDeletedAtIsNull(appointment.getListingId())
                .orElse(null);

        if (listing == null) {
            return false;
        }

        return listing.getStatus() == Status.RESERVED
                && appointment.getBuyerId().equals(listing.getReserverId());
    }

    private TradePromptResponse toTradePromptResponse(Appointment appointment) {
        ChatRoom chatRoom = chatRoomRepository.findByListingIdAndBuyerIdAndStatus(
                        appointment.getListingId(),
                        appointment.getBuyerId(),
                        ChatRoomStatus.ACTIVE
                )
                .orElseThrow(EntityNotFoundException::new);

        Member buyer = memberJpaRepository.findById(appointment.getBuyerId())
                .orElse(null);

        return new TradePromptResponse(
                appointment.getId(),
                appointment.getListingId(),
                chatRoom.getId(),
                appointment.getBuyerId(),
                buyer != null ? buyer.getNickname() : "상대방"
        );
    }
}
