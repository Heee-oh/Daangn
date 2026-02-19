package com.daangn.market.chat.domain;

import com.daangn.market.common.domain.id.MemberId;

import java.time.Instant;

public class ChatRead {
    Long roomId;
    MemberId memberId;
    long lastReadSeq;
    Instant updatedAt;
}
