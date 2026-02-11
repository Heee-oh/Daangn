package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.BaseTimeEntity;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Listing extends BaseTimeEntity {

    @EmbeddedId
    private ListingId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "seller_id"))
    private MemberId sellerId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "buyer_id"))
    private MemberId buyerId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "reserver_id"))
    private MemberId reserverId;
    private String title;
    private String description;
    private Integer categoryId;

    @Embedded
    private HopeLocation hopeLocation; // lat/lng

    @Enumerated(EnumType.STRING)
    private Status status;
    @Embedded
    private Price price;
    private boolean isHidden;

    private long viewCount;
    private long chatCount;

    private Instant deletedAt;

    @PrePersist
    protected void init() {
        if (id == null) {
            id = new ListingId(TsidCreator.getTsid().toLong());
        }
    }


    public static Listing draft() {
        Listing l = new Listing();
        l.status = Status.DRAFT;
        l.isHidden = false;
        return l;
    }

    public static Listing draftPrice(Long amount, boolean isFree) {
        Listing l = new Listing();
        l.status = Status.DRAFT;
        l.isHidden = false;
        l.updatePrice(amount, isFree);

        return l;
    }



    // 발행
    public void publish() {
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (status != Status.DRAFT) throw new IllegalStateException("발행 실패");

        status = Status.PUBLISHED;
    }

    // 숨기기
    public void hide() {
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (status == Status.DRAFT) throw new IllegalStateException("초안은 숨길 수 없음");

        isHidden = true;
    }

    // 숨기기 해제
    public void unHide() {
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (!isHidden) throw new IllegalStateException("이미 노출 상태");
        if (status == Status.DRAFT) throw new IllegalStateException("초안 상태");

        isHidden = false;
    }

    // 예약
    public void reserve(MemberId reservedId) {
        if (reservedId == null) throw new IllegalArgumentException("잘못된 예약자 id");
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (isHidden) throw new IllegalStateException("숨김 상태");
        if (status != Status.PUBLISHED) throw new IllegalStateException("예약 불가 상태");

        this.reserverId = reservedId;
        status = Status.RESERVED;
    }

    // 예약 취소
    public void cancelReserve() {
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (status != Status.RESERVED) throw new IllegalStateException("예약 상태 아님");

        status = Status.PUBLISHED;
        reserverId = null;
    }

    // 판매완료 처리
    public void markSoldOut(MemberId buyerId) {
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (reserverId == null || status != Status.RESERVED) throw new IllegalStateException("예약 상태 아님");
        if (!reserverId.equals(buyerId)) {
            throw new IllegalArgumentException("잘못된 구매자 id");
        }

        status = Status.SOLD_OUT;
        this.buyerId = reserverId;
    }

    // 삭제
    public void remove() {
        if (isDeleted()) throw new IllegalStateException("이미 삭제됨");
        if (status != Status.DRAFT && status != Status.PUBLISHED) {
            throw new IllegalStateException("삭제 조건 실패");
        }

        deletedAt = Instant.now();
    }

    // 가격 업데이트
    public void updatePrice(Long nPrice, boolean isFree) {
        ensureEditable();

        if (!isFree
                && ((nPrice == null) || nPrice <= 0))
            throw new IllegalArgumentException("잘못된 가격 설정");
        this.price = new Price(nPrice, isFree); // 무료면 자동으로 0원 세팅됨
    }

    // 타이틀과 설명 업데이트
    public void updateTitleAndDescription(String title, String description) {
        ensureEditable();

        if (title != null) {
            this.title = title;
        }

        if (description != null) {
            this.description = description;
        }
    }

    public void updateHopeLocation(HopeLocation newHopeLocation) {
        ensureEditable();
        hopeLocation = newHopeLocation;
    }

    public void updateCategory(Integer categoryId) {
        ensureEditable();
        this.categoryId = categoryId;
    }


    private void ensureEditable() {
        if (isDeleted()) throw new IllegalStateException("삭제된 게시글");
        if (status != Status.DRAFT && status != Status.PUBLISHED)
            throw new IllegalStateException("수정 불가 상태");
    }

    private boolean isDeleted() {
        return deletedAt != null;
    }

}
