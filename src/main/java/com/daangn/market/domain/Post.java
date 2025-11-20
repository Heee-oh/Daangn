package com.daangn.market.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int regionId;
    private long memberId;
    private int category;
    private String title;
    private String body;
    private int likeCnt;
    private int chatCnt;
    private int view;
    private int price;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    @Enumerated(EnumType.STRING)
    private TradeState state;
    private boolean isFree;

    @QueryProjection
    public Post(long id, int regionId, long memberId, int category, String title, String body, int likeCnt, int chatCnt, int view, int price, Point location, TradeState state, boolean isFree) {
        this.id = id;
        this.regionId = regionId;
        this.memberId = memberId;
        this.category = category;
        this.title = title;
        this.body = body;
        this.likeCnt = likeCnt;
        this.chatCnt = chatCnt;
        this.view = view;
        this.price = price;
        this.location = location;
        this.state = state;
        this.isFree = isFree;
    }
}
