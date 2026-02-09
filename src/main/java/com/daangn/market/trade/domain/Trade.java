package com.daangn.market.trade.domain;

import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.TradeId;

import java.time.Instant;

public class Trade {
    private TradeId id;
    private ListingId listingId;
    private MemberId sellerId;
    private MemberId buyerId;
    private Price price;
    private TradeStatus status; // CREATED/COMPLETED/CANCELED
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;

}
