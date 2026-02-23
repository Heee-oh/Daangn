package com.daangn.market.chat.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatReadId implements Serializable {
    private Long chatRoomId;
    private Long memberId;
}
