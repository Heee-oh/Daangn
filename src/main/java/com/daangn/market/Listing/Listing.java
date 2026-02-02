package com.daangn.market.Listing;

import jakarta.persistence.Embedded;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class Listing {
    private Long id;
    @Embedded
    private Status status;

    private boolean hidden;
    private Timestamp removedAt;
    private String reservedBy;
    private String purchasedBy;
    private boolean isFree;

    @Embedded
    private Price price;

    public Listing(Long price, boolean isFree) {
        status = Status.DRAFT;
        hidden = false;
        this.price = new Price(price, isFree);
        this.isFree = isFree;
    }

    public void publish() {
        if (removedAt == null && status == Status.DRAFT) {
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
                && removedAt == null) {
            hidden = true;
        }
    }

    public void unHide() {
        if (hidden && !isDeleted()
                && status != Status.DRAFT) {
            hidden = false;
        }
    }

    public void reserve(String reservedId) {
        if (!isDeleted()
                && !hidden
                && status == Status.PUBLISHED) {

            if (reservedId == null || reservedId.isBlank()) {
                throw new IllegalArgumentException("잘못된 예약자 id");
            }

            if (reservedBy != null) {
                throw new IllegalStateException("기존 예약자 존재");
            }

            reservedBy = reservedId;
            status = Status.RESERVED;
        } else {
            throw new IllegalStateException("예약 실패");
        }
    }

    public void cancelReserve() {
        if (!isDeleted() && status == Status.RESERVED) {
            status = Status.PUBLISHED;
            reservedBy = null;
        } else {
            throw new IllegalStateException("예약 상태가 아니거나 삭제된 게시물");
        }
    }

    public void markSold(String buyerId) {
        if (!isDeleted()
                && (status == Status.RESERVED || status == Status.PUBLISHED)) {

            if (buyerId == null || buyerId.isBlank()) {
                throw new IllegalArgumentException("잘못된 구매자 id");
            }

            status = Status.SOLD;
            purchasedBy = buyerId;
            reservedBy = null;
        } else {
            throw new IllegalStateException("sold 변경 실패");
        }
    }

    public void remove() {
        if (!isDeleted()
                && (status == Status.PUBLISHED || status == Status.DRAFT)) {
            removedAt = new Timestamp(System.currentTimeMillis());
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
        return removedAt != null;
    }

}
