package com.daangn.market.notification.domain;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.RegionId;
import com.daangn.market.common.domain.id.SubscriptionId;

public class KeywordSubscription {
    private SubscriptionId id;
    private MemberId memberId;
    private String keyword;      // 원본
    private RegionId regionId;   // nullable
    private java.time.Instant createdAt;

}
