package com.daangn.market.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@Table(name = "chat_read")
@IdClass(ChatReadId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRead {

    @Id
    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Id
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
