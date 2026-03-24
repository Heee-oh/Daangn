package com.daangn.market.chat.infrastructure;

import com.daangn.market.Listing.domain.QListing;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.domain.QChatMessage;
import com.daangn.market.chat.domain.QChatRoom;
import com.daangn.market.chat.presentation.dto.ChatRoomSummaryResponse;
import com.daangn.market.member.domain.QMember;
import com.daangn.market.region.domain.QRegion;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomRepositoryCustomImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QChatRoom chatRoom = QChatRoom.chatRoom;
    private final QListing listing = QListing.listing;
    private final QRegion region = QRegion.region;
    private final QMember seller = new QMember("seller");
    private final QMember buyer = new QMember("buyer");
    private final QChatMessage lastMessage = new QChatMessage("lastMessage");
    private final QChatMessage lastMessageSub = new QChatMessage("lastMessageSub");

    @Override
    public List<ChatRoomSummaryResponse> findSummariesByMemberId(Long memberId) {
        return queryFactory
                .select(Projections.constructor(
                        ChatRoomSummaryResponse.class,
                        chatRoom.id,
                        // case 문 챗방의 판매자가 조회했다면 구매자 닉네임을 상대방으로
                        new CaseBuilder()
                                .when(chatRoom.sellerId.eq(memberId)).then(buyer.nickname)
                                .otherwise(seller.nickname), // 아니라면 내가 구매자이기에 판매자 닉네임을
                        new CaseBuilder()
                                .when(chatRoom.sellerId.eq(memberId)).then(buyer.profileImageUrl)
                                .otherwise(seller.profileImageUrl),
                        region.dongnm,
                        lastMessage.content.coalesce(""),
                        lastMessage.createdAt.coalesce(chatRoom.createdAt)
                ))
                .from(chatRoom)
                .leftJoin(listing).on(chatRoom.listingId.eq(listing.id))
                .leftJoin(region).on(listing.regionId.eq(region.id))
                .leftJoin(seller).on(chatRoom.sellerId.eq(seller.id))
                .leftJoin(buyer).on(chatRoom.buyerId.eq(buyer.id))
                .leftJoin(lastMessage).on(
                        lastMessage.id.eq(
                                JPAExpressions // 마지막 메시지 필터
                                        .select(lastMessageSub.id.max())
                                        .from(lastMessageSub)
                                        .where(lastMessageSub.chatRoomId.eq(chatRoom.id))
                        )
                )
                .where(
                        chatRoom.status.eq(ChatRoomStatus.ACTIVE),
                        chatRoom.sellerId.eq(memberId).or(chatRoom.buyerId.eq(memberId))
                )
                .orderBy(
                        //coalesce 앞이 null이면 뒤에 값 사용 //sql에서는 COALESCE(A1,A2)
                        // 채팅이 있다면 가장 최근 마지막 메시지 시간순, 없다면 최근 챗방 생성 시간
                        lastMessage.createdAt.coalesce(chatRoom.createdAt).desc()
                )
                .fetch();
    }
}
