package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.MemberId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Member {
    private MemberId id;
    private String nickname;
    private MemberStatus status; // ACTIVE/SUSPENDED/WITHDRAWN
    private double mannerTemp;
    private PhoneNumber phoneNumber;
    private String profileImageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant withdrawnAt;

    private List<Interest> interests = new ArrayList<>();


}
