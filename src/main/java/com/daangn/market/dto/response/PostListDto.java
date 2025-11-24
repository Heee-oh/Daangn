package com.daangn.market.dto.response;

import com.daangn.market.domain.TradeStatus;
import com.daangn.market.dto.LocationDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListDto {
    private long id;
    private String postImageUrl;
    private String title;
    private LocalDateTime update;
    private int price;
    private int likeCnt;
    private int chatCnt;
    private TradeStatus status;

    private String dongnm;
    LocationDto preferredLocation;

    @QueryProjection
    public PostListDto(long id, String postImageUrl, String title, LocalDateTime update, int price, int likeCnt, int chatCnt, TradeStatus status, String dongnm, LocationDto preferredLocation) {
        this.id = id;
        this.postImageUrl = postImageUrl;
        this.title = title;
        this.update = update;
        this.price = price;
        this.likeCnt = likeCnt;
        this.chatCnt = chatCnt;
        this.status = status;
        this.dongnm = dongnm;
        this.preferredLocation = preferredLocation;
    }
}
