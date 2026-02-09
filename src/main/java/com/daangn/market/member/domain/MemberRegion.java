package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.RegionId;

import java.time.Instant;

public class MemberRegion {
    private MemberId memberId;
    private RegionId regionId;
    private boolean primary;
    private Instant verifiedAt; // null이거나 30일이 지났다면 인증만료
    private Instant createdAt;
}
