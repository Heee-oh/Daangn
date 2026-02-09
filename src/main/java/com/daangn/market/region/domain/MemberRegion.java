package com.daangn.market.region.domain;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.RegionId;

import java.time.Instant;

/**
 * 동네 인증 및 관리
 */
public class MemberRegion {
    private MemberId memberId;
    private RegionId regionId;
    private boolean isPrimary;

    private Instant verifiedAt;
    private Instant createdAt;

}
