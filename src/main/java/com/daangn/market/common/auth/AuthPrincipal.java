package com.daangn.market.common.auth;

import com.daangn.market.common.domain.id.MemberId;

import java.io.Serializable;

public record AuthPrincipal(MemberId memberId) implements Serializable { }
