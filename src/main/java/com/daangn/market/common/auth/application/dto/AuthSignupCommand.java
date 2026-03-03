package com.daangn.market.common.auth.application.dto;

public record AuthSignupCommand(
        String phoneNumber,
        String nickname
) {
}

