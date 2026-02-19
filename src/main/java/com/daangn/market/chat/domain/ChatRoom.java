package com.daangn.market.chat.domain;

import com.daangn.market.common.domain.id.ChatRoomId;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    @EmbeddedId
    private ChatRoomId id;
    private ListingId listingId;
    private MemberId sellerId;
    private MemberId buyerId;
    private ChatRoomStatus status; // ACTIVE/CLOSED
    private Instant createdAt;
    private Instant updatedAt;

    private final Map<MemberId, ChatRead> readState = new HashMap<>();


}
