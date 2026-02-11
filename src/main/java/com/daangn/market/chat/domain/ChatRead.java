package com.daangn.market.chat.domain;

import com.daangn.market.common.domain.id.ChatRoomId;
import com.daangn.market.common.domain.id.MemberId;

import java.time.Instant;

public record ChatRead(Long roomId, Long memberId, long lastReadSeq, Instant updatedAt) {}
