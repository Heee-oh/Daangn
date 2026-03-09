package com.daangn.market.chat.presentation.dto;

import com.daangn.market.chat.domain.MessageType;

import java.time.Instant;

public record ChatMessageResponse(Long chatRoomId, Long MessageId, Long senderId, MessageType type, String content, Instant createAt) {
}
