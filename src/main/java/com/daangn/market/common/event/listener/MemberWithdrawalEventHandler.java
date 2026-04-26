package com.daangn.market.common.event.listener;

import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import com.daangn.market.common.event.events.MemberWithdrawnEvent;
import com.daangn.market.member.infrastructure.memberRegion.MemberRegionJpaRepository;
import com.daangn.market.notification.infrastructure.KeywordSubscriptionRepository;
import com.daangn.market.notification.infrastructure.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberWithdrawalEventHandler {

    private final NotificationRepository notificationRepository;
    private final KeywordSubscriptionRepository keywordSubscriptionRepository;
    private final MemberRegionJpaRepository memberRegionJpaRepository;
    private final ListingJpaRepository listingJpaRepository;

    /**
     * 회원 탈퇴 이벤트를 구독하여 관련 알림, 구독, 동네 인증, 게시글을 정리한다.
     */
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        notificationRepository.deleteByMemberId(event.memberId());
        keywordSubscriptionRepository.deleteByMemberId(event.memberId());
        memberRegionJpaRepository.deleteByMember_Id(event.memberId());
        listingJpaRepository.hideAllBySellerId(event.memberId());
    }
}
