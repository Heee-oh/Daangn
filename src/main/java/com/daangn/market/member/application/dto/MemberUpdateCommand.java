package com.daangn.market.member.application.dto;

public record MemberUpdateCommand(
        String nickname,
        String profileImage
) {
}
