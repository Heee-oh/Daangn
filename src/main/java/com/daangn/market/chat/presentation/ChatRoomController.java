package com.daangn.market.chat.presentation;

import com.daangn.market.chat.application.ChatRoomService;
import com.daangn.market.chat.presentation.dto.ChatRoomCreateRequest;
import com.daangn.market.chat.presentation.dto.ChatRoomCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     *  채팅방 id 값을 획득하거나 생성
     */
    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> getOrCreateChatRoom(@RequestBody ChatRoomCreateRequest request) {
        Long chatRoomId = chatRoomService.getOrCreateChatRoom(
                request.listingId(),
                request.sellerId(),
                request.buyerId()
        );

        return ResponseEntity.ok(new ChatRoomCreateResponse(chatRoomId));
    }
}
