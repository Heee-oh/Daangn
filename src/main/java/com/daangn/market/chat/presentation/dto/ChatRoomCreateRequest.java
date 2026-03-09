package com.daangn.market.chat.presentation.dto;

public record ChatRoomCreateRequest(
        Long listingId,
        Long sellerId,
        Long buyerId
) {
}
