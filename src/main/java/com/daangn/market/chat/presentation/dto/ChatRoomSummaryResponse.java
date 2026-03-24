package com.daangn.market.chat.presentation.dto;

import java.time.Instant;

public record ChatRoomSummaryResponse(
        Long chatRoomId,
        String partnerNickname,
        String partnerProfileImage,
        String regionName,
        String lastMessage,
        Instant lastMessageAt
) {
}
