package com.daangn.market.member.domain;

import com.daangn.market.common.domain.BaseTimeEntity;
import com.daangn.market.common.domain.id.MemberId;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Member extends BaseTimeEntity {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "member_id"))
    private MemberId memberId;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberStatus status; // ACTIVE/SUSPENDED/WITHDRAWN
    private double mannerTemp;

    @Embedded
    private PhoneNumber phoneNumber;

    private String profileImageUrl;

    private Instant withdrawnAt;

    public Member() {
    }

    public Member(String nickname, PhoneNumber phoneNumber) {
        this.memberId = MemberId.generate();
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.status = MemberStatus.ACTIVE;
        this.mannerTemp = 36.5;
    }


    public void withdraw(Instant at) {
        if (status != MemberStatus.WITHDRAWN) {
            status = MemberStatus.WITHDRAWN;
            withdrawnAt = at;
        }
    }

    // -0.2 -0.1 0 0.1 0.2
    public void updateMannerTemp(int select) {
        ensureActive();

        double nMannerTemp = getMannerTemp(select);
        if (nMannerTemp < 0 || nMannerTemp > 99.9) {
            nMannerTemp = Math.max(0, Math.min(nMannerTemp, 99.9));
        }

        mannerTemp = nMannerTemp;
    }

    public void changeProfileImage(String fileName) {
        ensureActive();

        if (fileName == null) {
            throw new IllegalArgumentException("이미지가 없습니다.");
        }
        profileImageUrl = fileName;
    }

    public void changeNickname(String nickname) {
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


    private void ensureActive() {
        if (status != MemberStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태가 아님");
        }
    }

    private double getMannerTemp(int select) {
        return switch (select) {
            case 1 -> mannerTemp - 0.2;
            case 2 -> mannerTemp - 0.1;
            case 4 -> mannerTemp + 0.1;
            case 5 -> mannerTemp + 0.2;
            default -> mannerTemp;
        };
    }





}
