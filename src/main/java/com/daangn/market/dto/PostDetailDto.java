package com.daangn.market.dto;

import com.daangn.market.domain.PostImages;
import com.daangn.market.domain.TradeStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailDto {
    private long id;
    private long memberId;
    private String profileUrl;
    private String dongnm;
    private int mannerScore;
    private String title;
    private int price;
    private int category;
    private LocalDateTime update;
    private String body;
    private int likeCnt;
    private int viewCnt;
    private int chatCnt;
    private TradeStatus status;
    private LocationDto preferredLocation;
    private List<PostImages> postImages;

    @QueryProjection
    public PostDetailDto(long id, long memberId, String profileUrl, String dongnm, int mannerScore, String title,
                         int price, int category, LocalDateTime update, String body, int likeCnt, int viewCnt,
                         int chatCnt, TradeStatus status, LocationDto preferredLocation, List<PostImages> postImages) {
        this.id = id;
        this.memberId = memberId;
        this.profileUrl = profileUrl;
        this.dongnm = dongnm;
        this.mannerScore = mannerScore;
        this.title = title;
        this.price = price;
        this.category = category;
        this.update = update;
        this.body = body;
        this.likeCnt = likeCnt;
        this.viewCnt = viewCnt;
        this.chatCnt = chatCnt;
        this.status = status;
        this.preferredLocation = preferredLocation;
        this.postImages = postImages;
    }
}