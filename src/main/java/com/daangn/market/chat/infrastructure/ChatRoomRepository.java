package com.daangn.market.chat.infrastructure;

import com.daangn.market.chat.domain.ChatRoom;
import com.daangn.market.chat.domain.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    Optional<ChatRoom> findByListingIdAndBuyerIdAndStatus(
            Long listingId,
            Long buyerId,
            ChatRoomStatus status
    );

    long countByListingIdAndStatus(Long listingId, ChatRoomStatus status);
}
