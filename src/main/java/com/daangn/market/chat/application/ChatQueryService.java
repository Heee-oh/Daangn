package com.daangn.market.chat.application;

import com.daangn.market.chat.presentation.dto.ChatRoomDetailResponse;
import com.daangn.market.chat.presentation.dto.ChatRoomSummaryResponse;

import java.util.List;

public interface ChatQueryService {

    List<ChatRoomSummaryResponse> getChatRooms(Long memberId);

    ChatRoomDetailResponse getChatRoom(Long memberId, Long chatRoomId);
}
