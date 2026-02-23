package com.daangn.market.common.auth;

import java.io.Serializable;

public record AuthPrincipal(Long memberId) implements Serializable { }
