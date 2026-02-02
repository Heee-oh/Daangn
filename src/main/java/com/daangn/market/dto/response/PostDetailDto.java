package com.daangn.market.dto.response;

import com.daangn.market.domain.Location;
import com.daangn.market.domain.TradeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
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
    private double distance;

    private Location preferredLocation;
    private List<PostImageDto> postImages;



    public void initPostImages(List<PostImageDto> postImages) {
        this.postImages = postImages;
    }

    public PostDetailDto(long id, long memberId, String profileUrl, String dongnm, int mannerScore, String title, int price, int category, LocalDateTime update, String body, int likeCnt, int viewCnt, int chatCnt, TradeStatus status, double distance, double longitude, double latitude) {
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
        this.distance = distance;
        this.preferredLocation = new Location(longitude, latitude);
    }
}