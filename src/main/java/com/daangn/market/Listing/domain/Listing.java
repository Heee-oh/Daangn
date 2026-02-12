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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ListingImage> images = new ArrayList<>();

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

    // 선호 지역
    public void updateHopeLocation(HopeLocation newHopeLocation) {
        ensureEditable();
        hopeLocation = newHopeLocation;
    }

    // 카테고리 넘버
    public void updateCategory(Integer categoryId) {
        ensureEditable();
        this.categoryId = categoryId;
    }

    // 이미지 추가
    public void addImages(List<ListingImage> listingImages) {
        ensureEditable();
        listingImages.forEach(this::addImage);
    }

    // 이미지들 삭제
    public void deleteImages(List<Long> imageIds) {
        ensureEditable();

        HashSet<Long> idSet = new HashSet<>(imageIds);

        boolean removed = images.removeIf(image -> {
            if (idSet.contains(image.getImageId())) {
                image.updateListing(null);
                return true;
            }
            return false;
        });

        if (!removed) {
            throw new EntityNotFoundException("이미지 없음");
        }

        // 재정렬
        reorderImages();
    }

    // 이미지들 대체
    public void replaceImages(List<ListingImage> listingImages) {
        ensureEditable();

        // 기존 관계 끊기
        for (ListingImage img : images) {
            img.updateListing(null);
        }
        images.clear();

        // 새롭게 추가
        for (ListingImage image : listingImages) {
            addImage(image);
        }
    }


    public void updateImageOrder(List<Long> orderedImageIds) {
        ensureEditable();

        if (orderedImageIds.size() != images.size()) {
            throw new IllegalArgumentException("이미지 개수가 일치하지 않음");
        }

        Map<Long, ListingImage> map = images.stream()
                .collect(Collectors.toMap(
                        ListingImage::getImageId, // key mapper
                        Function.identity()) // value mapper (객체 그대로) (x -> x)와 동일
                );

        for (int i = 0; i < orderedImageIds.size(); i++) {
            ListingImage img = map.get(orderedImageIds.get(i));
            if (img == null) throw new EntityNotFoundException("이미지 존재하지 않음");
            img.updateSortOrder(i);
        }
    }

    // 편의 메서드
    private void addImage(ListingImage image) {
        images.add(image);
        image.updateListing(this);
        image.updateSortOrder(images.size() - 1);
    }


    private void reorderImages() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).updateSortOrder(i);
        }
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
