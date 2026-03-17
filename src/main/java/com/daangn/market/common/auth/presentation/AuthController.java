package com.daangn.market.common.auth.presentation;

import com.daangn.market.common.auth.application.AuthService;
import com.daangn.market.common.auth.application.dto.AuthTokenResponse;
import com.daangn.market.common.auth.presentation.dto.request.AuthLoginRequest;
import com.daangn.market.common.auth.presentation.dto.request.AuthSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthTokenResponse> signup(@Valid @RequestBody AuthSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signup(request.toCommand()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthTokenResponse login = authService.login(request.toCommand());
        return ResponseEntity.ok(login);
    }
}

