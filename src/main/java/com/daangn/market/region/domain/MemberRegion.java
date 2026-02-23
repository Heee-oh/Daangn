package com.daangn.market.region.domain;

import java.time.Instant;

/**
 * 동네 인증 및 관리
 */
public class MemberRegion {
    private Long memberId;
    private Integer regionId;
    private boolean isPrimary;

    private Instant verifiedAt;
    private Instant createdAt;

}
