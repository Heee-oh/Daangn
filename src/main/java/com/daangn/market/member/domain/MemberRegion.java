package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.RegionId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRegion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long MemberRegionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private RegionId regionId;
    private boolean primary;
    private Instant verifiedAt; // null이거나 30일이 지났다면 인증만료
    private Instant createdAt;


    private final static Duration VALIDITY_PERIOD = Duration.ofDays(30);

    public MemberRegion(RegionId regionId, boolean primary) {
        this.regionId = regionId;
        this.primary = primary;
        createdAt = verifiedAt = Instant.now();
    }

    public boolean isVerified() {
        if (verifiedAt == null) return false;

        return verifiedAt.plus(VALIDITY_PERIOD).isAfter(Instant.now());
    }

    // 동네 인증 스탬프 찍기
    public void verify() {
        this.verifiedAt = Instant.now();
    }

    // 인증기간 만료 체크
    public void verifyRegion() {
        if (!isVerified()) {
            throw new IllegalStateException("동네 인증 만료 재인증 요청");
        }
    }

    public void updateMember(Member member) {
        this.member = member;
        this.primary = true;
    }

    public void unsetPrimary() {
        this.primary = false;
    }
}
