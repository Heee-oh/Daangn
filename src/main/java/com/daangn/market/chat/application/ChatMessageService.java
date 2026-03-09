package com.daangn.market.chat.application;

import com.daangn.market.chat.domain.ChatMessage;
import com.daangn.market.chat.domain.ChatRead;
import com.daangn.market.chat.domain.ChatRoom;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.infrastructure.ChatMessageRepository;
import com.daangn.market.chat.infrastructure.ChatReadRepository;
import com.daangn.market.chat.infrastructure.ChatRoomRepository;
import com.daangn.market.chat.presentation.dto.ChatMessageRequest;
import com.daangn.market.chat.presentation.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {


    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadRepository chatReadRepository;

    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long senderId) {
        // 채팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        // 해당 방에 참여자인지 검증
        if (!chatRoom.isParticipant(senderId)) {
            throw new IllegalArgumentException("채팅방 참여자가 아닙니다.");
        }

        if (chatRoom.getStatus() != ChatRoomStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 채팅방입니다.");
        }

        // 메시지 저장
        ChatMessage message = ChatMessage.create(senderId, request.type(), request.content());
        ChatMessage saved = chatMessageRepository.save(message);

        // 발신자 read 상태 조회
        ChatRead senderRead
                = chatReadRepository.findByChatRoomIdAndMemberId(
                chatRoom.getId(), senderId
        ).orElseThrow(() -> new IllegalArgumentException("읽음 정보 없음"));

        // 읽음 표시
        senderRead.markAsRead(saved.getId());

        return new ChatMessageResponse(chatRoom.getId(), saved.getId(), saved.getSenderId(), saved.getType(), saved.getContent(), saved.getCreatedAt());
    }
}
