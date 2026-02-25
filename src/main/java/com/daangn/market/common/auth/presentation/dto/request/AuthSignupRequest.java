package com.daangn.market.common.auth.presentation.dto.request;

import com.daangn.market.common.auth.application.dto.AuthSignupCommand;
import jakarta.validation.constraints.NotBlank;

public record AuthSignupRequest(
        @NotBlank String phoneNumber,
        String nickname
) {
    public AuthSignupCommand toCommand() {
        return new AuthSignupCommand(phoneNumber, nickname);
    }
}

