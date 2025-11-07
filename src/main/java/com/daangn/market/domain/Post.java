package com.daangn.market.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int regionId;
    private long memberId;
    private int category;
    private String title;
    private String body;
    private int commentsCnt;
    private int likeCnt;
    private int chatCnt;
    private int view;
    private int price;
    private String location;

    @Enumerated(EnumType.STRING)
    private TradeStatus state;
    private boolean isFree;

    @QueryProjection
    public Post(long id, int regionId, long memberId, int category, String title, String body, int commentsCnt, int likeCnt, int chatCnt, int view, int price, String location, TradeStatus state, boolean isFree) {
        this.id = id;
        this.regionId = regionId;
        this.memberId = memberId;
        this.category = category;
        this.title = title;
        this.body = body;
        this.commentsCnt = commentsCnt;
        this.likeCnt = likeCnt;
        this.chatCnt = chatCnt;
        this.view = view;
        this.price = price;
        this.location = location;
        this.state = state;
        this.isFree = isFree;
    }
}
