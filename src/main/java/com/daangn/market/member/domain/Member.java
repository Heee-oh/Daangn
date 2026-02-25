package com.daangn.market.member.domain;

import com.daangn.market.common.domain.BaseTimeEntity;
import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MemberStatus status;

    @Column(name = "manner_temp", nullable = false)
    private int mannerTemp;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(name = "profile_image", length = 500)
    private String profileImageUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MemberRegion> regions = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Interest> interests = new ArrayList<>();

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    @PrePersist
    void init() {
        if (id == null) {
            id = TsidCreator.getTsid().toLong();
        }
    }

    public Member(String nickname, PhoneNumber phoneNumber) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.status = MemberStatus.ACTIVE;
        this.mannerTemp = 365;
    }

    public void withdraw() {
        if (status != MemberStatus.WITHDRAWN) {
            status = MemberStatus.WITHDRAWN;
            withdrawnAt = Instant.now();
            return;
        }
        throw new IllegalStateException("Already withdrawn member");
    }

    public void updateMannerTemp(int select) {
        ensureActive();

        int nMannerTemp = getMannerTemp(select);
        if (nMannerTemp < 0 || nMannerTemp > 999) {
            nMannerTemp = Math.max(0, Math.min(nMannerTemp, 999));
        }

        mannerTemp = nMannerTemp;
    }

    public void updateProfileImage(String fileName) {
        ensureActive();

        if (fileName == null) {
            throw new IllegalArgumentException("Image is required");
        }
        profileImageUrl = fileName;
    }

    public void updateNickname(String nickname) {
        ensureActive();

        if (nickname == null || nickname.isBlank() || nickname.length() > 50) {
            throw new IllegalArgumentException("Nickname must be 1 to 50 characters");
        }

        this.nickname = nickname;
    }

    public void suspend() {
        ensureActive();
        status = MemberStatus.SUSPENDED;
    }

    public void active() {
        if (status != MemberStatus.SUSPENDED) {
            throw new IllegalStateException("Status is not SUSPENDED");
        }

        status = MemberStatus.ACTIVE;
    }

    public void addRegion(MemberRegion region) {
        ensureActive();

        regions.add(region);
        region.updateMember(this);
    }

    public void removeRegionFromPrimary(Integer regionId) {
        ensureActive();

        MemberRegion region = regions.stream()
                .filter(r -> r.getRegionId().equals(regionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Region not found"));

        region.unsetPrimary();
    }

    public void addInterest(Interest interest) {
        this.interests.add(interest);
        interest.updateMember(this);
    }

    private void ensureActive() {
        if (status != MemberStatus.ACTIVE) {
            throw new IllegalStateException("Status is not ACTIVE");
        }
    }

    private int getMannerTemp(int select) {
        return switch (select) {
            case 1 -> mannerTemp - 2;
            case 2 -> mannerTemp - 1;
            case 4 -> mannerTemp + 1;
            case 5 -> mannerTemp + 2;
            default -> mannerTemp;
        };
    }
}
