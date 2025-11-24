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
    private TradeStatus state;
    private int distance;

    private Location preferredLocation;
    private List<PostImageDto> postImages;



    public void initPostImages(List<PostImageDto> postImages) {
        this.postImages = postImages;
    }
}