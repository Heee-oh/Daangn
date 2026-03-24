package com.daangn.market.chat.presentation.dto;

import java.util.List;

public record ChatRoomDetailResponse(
        Long chatRoomId,
        Long listingId,
        String partnerNickname,
        String partnerProfileImage,
        Integer partnerMannerTemp,
        String listingTitle,
        Long listingPrice,
        List<ChatMessageItemResponse> messages
) {
}
