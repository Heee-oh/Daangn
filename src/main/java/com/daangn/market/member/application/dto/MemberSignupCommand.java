package com.daangn.market.member.application.dto;

import com.daangn.market.member.domain.PhoneNumber;

public record MemberSignupCommand(
        String nickname,
        PhoneNumber phoneNumber
) {
}
