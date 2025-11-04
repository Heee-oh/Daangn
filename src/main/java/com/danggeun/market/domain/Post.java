package com.danggeun.market.domain;

import jakarta.persistence.*;

@Entity
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
    private int view;
    private int price;
    private String location;

    @Enumerated(EnumType.STRING)
    private TradeStatus state;
    private boolean isFree;


}
