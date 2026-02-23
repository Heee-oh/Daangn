package com.daangn.market.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

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
    @JoinColumn(name = "member_id", nullable = false, insertable = false, updatable = false)
    private Member member;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private static final Duration VALIDITY_PERIOD = Duration.ofDays(30);

    public MemberRegion(Integer regionId, boolean primary) {
        this.regionId = regionId;
        this.primary = primary;
        this.createdAt = Instant.now();
        this.verifiedAt = Instant.now();
    }

    public boolean isVerified() {
        if (verifiedAt == null) {
            return false;
        }
        return verifiedAt.plus(VALIDITY_PERIOD).isAfter(Instant.now());
    }

    public void verify() {
        this.verifiedAt = Instant.now();
    }

    public void verifyRegion() {
        if (!isVerified()) {
            throw new IllegalStateException("Region verification expired");
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
