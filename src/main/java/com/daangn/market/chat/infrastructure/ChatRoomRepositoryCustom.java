package com.daangn.market.chat.infrastructure;

import com.daangn.market.chat.presentation.dto.ChatRoomSummaryResponse;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoomSummaryResponse> findSummariesByMemberId(Long memberId);
}
