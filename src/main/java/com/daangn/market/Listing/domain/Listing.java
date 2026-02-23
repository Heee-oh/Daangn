package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.BaseTimeEntity;
import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "listing")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Listing extends BaseTimeEntity {

    @Id
    @Column(name = "listing_id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "reserver_id")
    private Long reserverId;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @Embedded
    private HopeLocation hopeLocation;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status;

    @Embedded
    private Price price;

    @Column(name = "is_hidden")
    private boolean isHidden;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ListingImage> images = new ArrayList<>();

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void init() {
        if (id == null) {
            id = TsidCreator.getTsid().toLong();
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

    public void publish() {
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (status != Status.DRAFT) {
            throw new IllegalStateException("Publish failed");
        }

        status = Status.PUBLISHED;
    }

    public void hide() {
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (status == Status.DRAFT) {
            throw new IllegalStateException("Draft cannot be hidden");
        }

        isHidden = true;
    }

    public void unHide() {
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (!isHidden) {
            throw new IllegalStateException("Already visible");
        }
        if (status == Status.DRAFT) {
            throw new IllegalStateException("Draft state");
        }

        isHidden = false;
    }

    public void reserve(Long reservedId) {
        if (reservedId == null) {
            throw new IllegalArgumentException("Invalid buyer id");
        }
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (isHidden) {
            throw new IllegalStateException("Hidden state");
        }
        if (status != Status.PUBLISHED) {
            throw new IllegalStateException("Cannot reserve in current status");
        }

        this.reserverId = reservedId;
        status = Status.RESERVED;
    }

    public void cancelReserve() {
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (status != Status.RESERVED) {
            throw new IllegalStateException("Not reserved state");
        }

        status = Status.PUBLISHED;
        this.reserverId = null;
    }

    public void markSoldOut(Long buyerId) {
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (status != Status.RESERVED) {
            throw new IllegalStateException("Not reserved state");
        }
        if (!this.reserverId.equals(buyerId)) {
            throw new IllegalArgumentException("Invalid buyer id");
        }

        status = Status.SOLD_OUT;
        this.buyerId = buyerId;
    }

    public void remove() {
        if (isDeleted()) {
            throw new IllegalStateException("Already deleted");
        }
        if (status != Status.DRAFT && status != Status.PUBLISHED) {
            throw new IllegalStateException("Delete precondition failed");
        }

        deletedAt = Instant.now();
    }

    public void updatePrice(Long nPrice, boolean isFree) {
        ensureEditable();

        if (!isFree && (nPrice == null || nPrice <= 0)) {
            throw new IllegalArgumentException("Invalid price");
        }
        this.price = new Price(nPrice, isFree);
    }

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

    public void updateCategory(Long categoryId) {
        ensureEditable();
        this.categoryId = categoryId;
    }

    public void addImages(List<ListingImage> listingImages) {
        ensureEditable();
        listingImages.forEach(this::addImage);
    }

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
            throw new EntityNotFoundException("Image not found");
        }

        reorderImages();
    }

    public void replaceImages(List<ListingImage> listingImages) {
        ensureEditable();

        for (ListingImage img : images) {
            img.updateListing(null);
        }
        images.clear();

        for (ListingImage image : listingImages) {
            addImage(image);
        }
    }

    public void updateImageOrder(List<Long> orderedImageIds) {
        ensureEditable();

        if (orderedImageIds.size() != images.size()) {
            throw new IllegalArgumentException("Image count mismatch");
        }

        Map<Long, ListingImage> map = images.stream()
                .collect(Collectors.toMap(ListingImage::getImageId, Function.identity()));

        for (int i = 0; i < orderedImageIds.size(); i++) {
            ListingImage img = map.get(orderedImageIds.get(i));
            if (img == null) {
                throw new EntityNotFoundException("Image not found");
            }
            img.updateSortOrder(i);
        }
    }

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
        if (isDeleted()) {
            throw new IllegalStateException("Deleted listing");
        }
        if (status != Status.DRAFT && status != Status.PUBLISHED) {
            throw new IllegalStateException("Cannot edit in current state");
        }
    }

    private boolean isDeleted() {
        return deletedAt != null;
    }
}
