package com.daangn.market.chat.domain;

import com.daangn.market.common.domain.id.ChatRoomId;
import com.daangn.market.common.domain.id.MemberId;

import java.time.Instant;

public record ChatRead(ChatRoomId roomId, MemberId memberId, long lastReadSeq, Instant updatedAt) {}
