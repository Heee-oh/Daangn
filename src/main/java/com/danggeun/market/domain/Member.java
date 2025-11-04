package com.danggeun.market.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private String publicId;
    // 매너 온도, 정수로 저장, 사용시 / 10 ex) 36.5 -> 365 / 10
    private int mannerScore;
    // 휴대폰 번호
    private String phoneNumber;
    // 사용자 식별 코드 (이를 통해 중복 닉네임 가능)
    private String tag;
    // 계정 활성 여부
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
}
