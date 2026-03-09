package com.daangn.market.chat.presentation.dto;

import com.daangn.market.chat.domain.MessageType;

public record ChatMessageRequest(Long chatRoomId,  MessageType type, String content) {
}
