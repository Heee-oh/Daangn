package com.daangn.market.chat.application;

import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import com.daangn.market.chat.domain.ChatRoom;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.infrastructure.ChatMessageRepository;
import com.daangn.market.chat.infrastructure.ChatRoomRepository;
import com.daangn.market.chat.presentation.dto.ChatMessageItemResponse;
import com.daangn.market.chat.presentation.dto.ChatRoomDetailResponse;
import com.daangn.market.chat.presentation.dto.ChatRoomSummaryResponse;
import com.daangn.market.member.domain.Member;
import com.daangn.market.member.infrastructure.member.MemberJpaRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryServiceImpl implements ChatQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ListingJpaRepository listingJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public List<ChatRoomSummaryResponse> getChatRooms(Long memberId) {
        return chatRoomRepository.findSummariesByMemberId(memberId);
    }

    /**
     * 채팅방 조회
     * @param memberId
     * @param chatRoomId
     * @return
     */
    @Override
    public ChatRoomDetailResponse getChatRoom(Long memberId, Long chatRoomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .filter(room -> room.getStatus() == ChatRoomStatus.ACTIVE) // 활동중인 챗방인지
                .filter(room -> room.isParticipant(memberId)) // 참가자로 되어있는 지
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 챗방에 연결된 판매글 조회
        Listing listing = listingJpaRepository.findActiveByIdWithImages(chatRoom.getListingId())
                .orElse(null);

        // 상대방 조회
        Long partnerId = getPartnerId(chatRoom, memberId);
        Member partner = memberJpaRepository.findById(partnerId)
                .orElse(null);

        //TODO 메시지 전부 조회를 바꿔야함 페이지단위? 아니면 Slice 추후 고민

        // 챗방메시지 전부 조회
        List<ChatMessageItemResponse> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAscIdAsc(chatRoomId)
                .stream()
                .map(message -> new ChatMessageItemResponse(
                        message.getId(),
                        message.getSenderId(),
                        message.getType(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .toList();

        return new ChatRoomDetailResponse(
                chatRoom.getId(),
                chatRoom.getListingId(),
                partner != null ? partner.getNickname() : "당근 사용자",
                partner != null ? partner.getProfileImageUrl() : null,
                partner != null ? partner.getMannerTemp() : null,
                listing != null ? listing.getTitle() : "상품 정보를 불러올 수 없어요.",
                listing != null && listing.getPrice() != null ? listing.getPrice().getPriceAmount() : null,
                messages
        );
    }

    /**
     * 상대방 id 구별
     * @param chatRoom
     * @param memberId
     * @return
     */
    private Long getPartnerId(ChatRoom chatRoom, Long memberId) {
        if (Objects.equals(chatRoom.getSellerId(), memberId)) {
            return chatRoom.getBuyerId();
        }
        return chatRoom.getSellerId();
    }
}
