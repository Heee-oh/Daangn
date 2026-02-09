package com.daangn.market.notification.domain;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.NotificationId;

import java.time.Instant;

public class Notification {
    private NotificationId id;
    private MemberId memberId;
    private NotificationType type;
    private boolean read;
    private Instant createdAt;
    private Instant readAt;
}
