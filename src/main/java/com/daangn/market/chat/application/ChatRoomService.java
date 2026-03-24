package com.daangn.market.chat.application;

import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import com.daangn.market.chat.domain.ChatRead;
import com.daangn.market.chat.domain.ChatRoom;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.infrastructure.ChatReadRepository;
import com.daangn.market.chat.infrastructure.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatReadRepository chatReadRepository;
    private final ListingJpaRepository listingJpaRepository;

    @Transactional
    public Long getOrCreateChatRoom(Long listingId, Long buyerId) {
        Long sellerId = listingJpaRepository.findByIdAndDeletedAtIsNull(listingId)
                .orElseThrow(EntityNotFoundException::new)
                .getSellerId();

        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByListingIdAndBuyerIdAndStatus(listingId, buyerId, ChatRoomStatus.ACTIVE);

        if (existingRoom.isPresent()) {
            ChatRoom room = existingRoom.get();
            if (!room.getSellerId().equals(sellerId)) {
                room.syncSellerId(sellerId);
            }
            return room.getId();
        }

        ChatRoom chatRoom = ChatRoom.create(listingId, sellerId, buyerId);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        ChatRead buyerRead = ChatRead.create(savedRoom.getId(), buyerId);
        ChatRead sellerRead = ChatRead.create(savedRoom.getId(), sellerId);

        chatReadRepository.save(buyerRead);
        chatReadRepository.save(sellerRead);

        return savedRoom.getId();
    }
}
