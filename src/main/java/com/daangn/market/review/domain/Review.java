package com.daangn.market.review.domain;

import com.daangn.market.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "listing_id", nullable = false)
    private Long listingId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "is_seller", nullable = false)
    private boolean isSeller;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
