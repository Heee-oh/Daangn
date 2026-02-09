package com.daangn.market.review.domain;

import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.ReviewId;
import com.daangn.market.common.domain.id.TradeId;

import java.time.Instant;

public class Review {
    private ReviewId id;
    private TradeId tradeId;
    private ListingId listingId;
    private MemberId writerId;
    private MemberId targetId;
    private boolean isSeller;
    private int rating; // 1~5
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;

}
