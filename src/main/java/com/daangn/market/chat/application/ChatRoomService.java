package com.daangn.market.chat.application;

import com.daangn.market.chat.domain.ChatRead;
import com.daangn.market.chat.domain.ChatRoom;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.infrastructure.ChatReadRepository;
import com.daangn.market.chat.infrastructure.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatReadRepository chatReadRepository;

    @Transactional
    public Long getOrCreateChatRoom(Long listingId, Long sellerId, Long buyerId) {
        // 기존 챗방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByListingIdAndBuyerIdAndStatus(listingId, buyerId, ChatRoomStatus.ACTIVE);

        // 이미 있다면 방 id 반환
        if (existingRoom.isPresent()) {
            return existingRoom.get().getId();
        }

        ChatRoom chatRoom = ChatRoom.create(listingId, sellerId, buyerId);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 두 유저의 읽음 처리 데이터 생성 및 저장
        ChatRead buyerRead = ChatRead.create(savedRoom.getId(), buyerId);
        ChatRead sellerRead = ChatRead.create(savedRoom.getId(), sellerId);

        chatReadRepository.save(buyerRead);
        chatReadRepository.save(sellerRead);

        // 새 방 id 반환
        return savedRoom.getId();
    }


}
