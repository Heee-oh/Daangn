package com.daangn.market.member.domain;

import com.daangn.market.common.domain.BaseTimeEntity;
import com.daangn.market.common.domain.id.MemberId;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "member_id"))
    private MemberId memberId;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberStatus status; // ACTIVE/SUSPENDED/WITHDRAWN
    private int mannerTemp;

    @Embedded
    private PhoneNumber phoneNumber;
    private String profileImageUrl;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MemberRegion> regions = new ArrayList<>();

    private Instant withdrawnAt;


    public Member(String nickname, PhoneNumber phoneNumber) {
        this.memberId = MemberId.generate();
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.status = MemberStatus.ACTIVE;
        this.mannerTemp = 365;
    }


    public void withdraw() {
        if (status != MemberStatus.WITHDRAWN) {
            status = MemberStatus.WITHDRAWN;
            withdrawnAt = Instant.now();
        }
    }

    // -0.2 -0.1 0 0.1 0.2
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
            throw new IllegalArgumentException("이미지가 없습니다.");
        }
        profileImageUrl = fileName;
    }

    public void updateNickname(String nickname) {
        ensureActive();

        if (nickname == null || nickname.isBlank() || nickname.length() > 50) {
            throw new IllegalArgumentException("닉네임이 없거나 50자를 넘었습니다.");
        }

        this.nickname = nickname;
    }

    public void suspend() {
        ensureActive();
        status = MemberStatus.SUSPENDED;
    }

    public void active() {
        if (status != MemberStatus.SUSPENDED){
            throw new IllegalStateException("정지 상태가 아님");
        }

        status = MemberStatus.ACTIVE;
    }

    public void addRegion(MemberRegion region) {
        ensureActive();

        long activeCount = regions.stream()
                .filter(MemberRegion::isPrimary)
                .count();

        if (activeCount >= 2) {
            throw new IllegalStateException("동네는 최대 2개 설정 가능");
        }

        regions.add(region);
        region.updateMember(this);
    }

    // 메인인 2개 지역에서 제외
    public void removeRegionFromPrimary(Long memberRegionId) {
        ensureActive();

        MemberRegion region = regions.stream()
                .filter(r -> r.getMemberRegionId().equals(memberRegionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("지역 없음"));

        region.unsetPrimary();
    }



    private void ensureActive() {
        if (status != MemberStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태가 아님");
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
