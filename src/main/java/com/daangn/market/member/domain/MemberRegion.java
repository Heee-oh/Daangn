package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.RegionId;

import java.time.Duration;
import java.time.Instant;

public class MemberRegion {
    private MemberId memberId;
    private RegionId regionId;
    private boolean primary;
    private Instant verifiedAt; // null이거나 30일이 지났다면 인증만료
    private Instant createdAt;


    private final static Duration VALIDITY_PERIOD = Duration.ofDays(30);

    public MemberRegion(MemberId memberId, RegionId regionId, boolean primary) {
        this.memberId = memberId;
        this.regionId = regionId;
        this.primary = primary;
        createdAt = verifiedAt = Instant.now();
    }

    public boolean isVerified() {
        if (verifiedAt == null) return false;

        return verifiedAt.plus(VALIDITY_PERIOD).isAfter(Instant.now());
    }

    public void verify() {
        this.verifiedAt = Instant.now();
    }

    public void verifyRegion() {
        if (!isVerified()) {
            throw new IllegalStateException("동네 인증 만료 재인증 요청");
        }
    }
}
