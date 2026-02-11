package com.daangn.market.common.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class ChatRoomId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "chat_room_id")
    private Long value;

    protected ChatRoomId() {
        // JPA 기본 생성자
    }

    public ChatRoomId(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }

    // equals / hashCode 필수 (식별자이므로)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoomId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}