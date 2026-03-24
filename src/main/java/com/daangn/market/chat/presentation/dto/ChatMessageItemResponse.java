package com.daangn.market.chat.presentation.dto;

import com.daangn.market.chat.domain.MessageType;

import java.time.Instant;

public record ChatMessageItemResponse(
        Long messageId,
        Long senderId,
        MessageType type,
        String content,
        Instant createdAt
) {
}
