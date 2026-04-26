package com.daangn.market.notification.infrastructure;

import com.daangn.market.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    void deleteByMemberId(Long memberId);
}
