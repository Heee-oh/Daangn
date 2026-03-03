package com.daangn.market.common.auth.application;

import com.daangn.market.common.auth.application.dto.AuthLoginCommand;
import com.daangn.market.common.auth.application.dto.AuthSignupCommand;
import com.daangn.market.common.auth.application.dto.AuthTokenResponse;

public interface AuthService {
    AuthTokenResponse signup(AuthSignupCommand command);

    AuthTokenResponse login(AuthLoginCommand command);
}

