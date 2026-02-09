package com.daangn.market.chat.domain;

import com.daangn.market.common.domain.id.ChatRoomId;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    private ChatRoomId id;
    private ListingId listingId;
    private MemberId sellerId;
    private MemberId buyerId;
    private ChatRoomStatus status; // ACTIVE/CLOSED
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;

    private final Map<MemberId, ChatRead> readState = new HashMap<>();


}
