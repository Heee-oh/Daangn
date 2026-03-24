package com.daangn.market.member.domain;

import com.daangn.market.member.domain.exception.memberRegion.RegionVerificationExpiredException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_region")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_region_id")
    private Long id;

    @Column(name = "region_id", nullable = false)
    private Integer regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private static final Duration VALIDITY_PERIOD = Duration.ofDays(30);

    public MemberRegion(Integer regionId, boolean primary) {
        Instant now = Instant.now();
        this.regionId = regionId;
        this.primary = primary;
        this.createdAt = now;
        this.verifiedAt = now;
    }

    public static MemberRegion unverified(Integer regionId, boolean primary) {
        MemberRegion memberRegion = new MemberRegion();
        memberRegion.regionId = regionId;
        memberRegion.primary = primary;
        memberRegion.createdAt = Instant.now();
        memberRegion.verifiedAt = null;
        return memberRegion;
    }

    public boolean isVerified() {
        return !isExpired(Instant.now());
    }

    public void verify(Instant now) {
        if (isExpired(now)) {
            this.verifiedAt = now;
        }
    }

    public void checkVerification() {
        if (!isVerified()) {
            throw new RegionVerificationExpiredException("Region verification expired");
        }
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void unsetPrimary() {
        this.primary = false;
    }

    private boolean isExpired(Instant now) {
        return verifiedAt == null
                || verifiedAt.plus(VALIDITY_PERIOD).isBefore(now);
    }
}
