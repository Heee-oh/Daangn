package com.danggeun.market.domain;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDGenerator;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // 닉네임
    private String nickname;
    // 이름
    private String name;

    // UUIDv7 외부용 id
    @Column(columnDefinition = "BINARY(16)")
    private UUID publicId;

    // 매너 온도, 정수로 저장, 사용시 / 10 ex) 36.5 -> 365 / 10
    private int mannerScore;
    // 휴대폰 번호
    private String phoneNumber;
    // 사용자 식별 코드 (이를 통해 중복 닉네임 가능)
    private String tag;
    // 계정 활성 여부
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @PrePersist
    void init() {
        if (publicId == null) {
            publicId = Generators.timeBasedEpochRandomGenerator()
                    .generate();
        }

        if (tag == null) {
            SecureRandom random = new SecureRandom();
            long value = random.nextLong();
            long positive = value < 0 ? -value : value;
            String base36 = Long.toString(positive, 36).toUpperCase();
            tag = base36
                    .replace(' ','0') // 빈 칸 제로 패딩
                    .substring(0, 7);
        }
    }
    @Builder
    public Member(String nickname, String name, String phoneNumber) {
        this.nickname = nickname;
        this.name = name;
        this.phoneNumber = phoneNumber;

        this.mannerScore = 365;

        this.status = MemberStatus.ACTIVE;

    }
}
