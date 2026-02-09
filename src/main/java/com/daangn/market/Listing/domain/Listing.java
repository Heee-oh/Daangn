package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import jakarta.persistence.Embedded;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Listing {
    private ListingId id;
    private MemberId sellerId;
    private MemberId buyerId;
    private MemberId reserverId;

    private String title;
    private String description;
    private Long categoryId;
    private HopeLocation hopeLocation; // lat/lng
    private Status status;
    @Embedded
    private Price price;
    private boolean isHidden;
    private boolean isFree;

    private long viewCount;
    private long chatCount;

    private final Set<MemberId> waiters = new HashSet<>();
    private final List<ListingImage> images = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;


    public Listing(Long price, boolean isFree) {
        status = Status.DRAFT;
        isHidden = false;
        this.price = new Price(price, isFree);
        this.isFree = isFree;
    }

    public void publish() {
        if (deletedAt == null && status == Status.DRAFT) {
            if (price == null && !isFree) {
                throw new IllegalArgumentException("나눔이 아닌데 price가 null"); // 임시
            }

            if (isFree) price = new Price(0L, true);
            else if (price.price() <= 0) {
                throw new IllegalArgumentException("나눔이 아닌데 가격이 0이하");
            }

            status = Status.PUBLISHED;
        } else {

            throw new IllegalStateException("이미 발행된 게시글");
        }
    }

    public void hide() {
        if (status != Status.DRAFT
                && deletedAt == null) {
            isHidden = true;
        }
    }

    public void unHide() {
        if (isHidden && !isDeleted()
                && status != Status.DRAFT) {
            isHidden = false;
        }
    }

    public void reserve(MemberId reservedId) {
        if (!isDeleted()
                && !isHidden
                && status == Status.PUBLISHED) {

            if (reservedId == null) {
                throw new IllegalArgumentException("잘못된 예약자 id");
            }

            if (buyerId != null) {
                throw new IllegalStateException("기존 예약자 존재");
            }

            buyerId = reservedId;
            status = Status.RESERVED;
        } else {
            throw new IllegalStateException("예약 실패");
        }
    }

    public void cancelReserve() {
        if (!isDeleted() && status == Status.RESERVED) {
            status = Status.PUBLISHED;
            buyerId = null;
        } else {
            throw new IllegalStateException("예약 상태가 아니거나 삭제된 게시물");
        }
    }

    public void markSoldOut(MemberId buyerId) {
        if (!isDeleted()
                && (status == Status.RESERVED)) {
            if (buyerId == null || this.buyerId != buyerId) {
                throw new IllegalArgumentException("잘못된 구매자 id");
            }

            status = Status.SOLD_OUT;
            this.buyerId = buyerId;

        } else {
            throw new IllegalStateException("sold_out 변경 실패");
        }
    }

    public void remove() {
        if (!isDeleted()
                && (status == Status.PUBLISHED || status == Status.DRAFT)) {
            deletedAt = Instant.now();
        } else {
            throw new IllegalStateException("삭제 조건 실패");
        }
    }

    // 임시로 가격 변경만

    public void update(long price) {
        if (!isDeleted()
                && (status == Status.PUBLISHED || status == Status.DRAFT)) {

            if (isFree) throw new IllegalStateException("무료인데 가격 변경 시도");
            else if (price <= 0) throw new IllegalArgumentException("무료가 아닌데 0 이하 가격 수정시도");

            this.price = new Price(price, this.price.isFree());
        } else {
            throw new IllegalStateException("변경 실패");
        }
    }

    private boolean isDeleted() {
        return deletedAt != null;
    }

}
