package com.daangn.market.chat.presentation;

import com.daangn.market.chat.application.ChatQueryService;
import com.daangn.market.chat.application.ChatRoomService;
import com.daangn.market.chat.presentation.dto.ChatRoomCreateRequest;
import com.daangn.market.chat.presentation.dto.ChatRoomCreateResponse;
import com.daangn.market.chat.presentation.dto.ChatRoomDetailResponse;
import com.daangn.market.chat.presentation.dto.ChatRoomSummaryResponse;
import com.daangn.market.common.auth.AuthPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatQueryService chatQueryService;

    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> getOrCreateChatRoom(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody ChatRoomCreateRequest request) {
        Long chatRoomId = chatRoomService.getOrCreateChatRoom(
                request.listingId(),
                principal.memberId()
        );

        return ResponseEntity.ok(new ChatRoomCreateResponse(chatRoomId));
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomSummaryResponse>> getChatRooms(
            @AuthenticationPrincipal AuthPrincipal principal) {

        return ResponseEntity.ok(chatQueryService.getChatRooms(principal.memberId()));
    }


    @GetMapping("/{chat_room_id}")
    public ResponseEntity<ChatRoomDetailResponse> getChatRoom(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("chat_room_id") Long chatRoomId) {

        return ResponseEntity.ok(chatQueryService.getChatRoom(principal.memberId(), chatRoomId));
    }
}
