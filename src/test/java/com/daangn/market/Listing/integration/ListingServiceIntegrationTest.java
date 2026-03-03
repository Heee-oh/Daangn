package com.daangn.market.Listing.integration;

import com.daangn.market.Listing.application.ListingCommandService;
import com.daangn.market.Listing.application.ListingQueryService;
import com.daangn.market.Listing.application.dto.ListingDetailResponse;
import com.daangn.market.Listing.application.dto.ListingUpdateCommand;
import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.domain.Status;
import com.daangn.market.Listing.exception.ListingNotFoundException;
import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ListingServiceIntegrationTest {

    @Autowired
    private ListingCommandService listingCommandService;

    @Autowired
    private ListingQueryService listingQueryService;

    @Autowired
    private ListingJpaRepository listingJpaRepository;

    @Test
    @DisplayName("데이터베이스 연동: 임시 글 생성 후 수정으로 값 저장이 가능하다")
    void createDraftAndUpdateListing() {
        Long listingId = listingCommandService.createDraft(100L,1);

        listingCommandService.update(100L, listingId, new ListingUpdateCommand(
                "iPhone",
                "good condition",
                1L,
                700000L,
                false,
                11000,
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"),
                List.of("https://img/1.png", "https://img/2.png")
        ));

        ListingDetailResponse detail = listingQueryService.getListing(listingId);

        assertThat(detail.listingId()).isEqualTo(listingId);
        assertThat(detail.sellerId()).isEqualTo(100L);
        assertThat(detail.title()).isEqualTo("iPhone");
        assertThat(detail.priceAmount()).isEqualTo(700000L);
        assertThat(detail.images()).hasSize(2);
    }

    @Test
    @DisplayName("데이터베이스 연동: 게시에서 예약 후 판매 완료까지 상태 전이가 영속화된다")
    void statusFlowIsPersisted() {
        Long listingId = listingCommandService.createDraft(100L, 1);

        listingCommandService.update(100L, listingId, new ListingUpdateCommand(
                "Macbook",
                "almost new",
                1L,
                1200000L,
                false,
                11000,
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"),
                List.of("https://img/1.png")
        ));

        listingCommandService.publish(100L, listingId);
        listingCommandService.reserve(100L, listingId, 200L);
        listingCommandService.markSoldOut(100L, listingId, 200L);

        Listing listing = listingJpaRepository.findByIdAndDeletedAtIsNull(listingId).orElseThrow();

        assertThat(listing.getStatus()).isEqualTo(Status.SOLD_OUT);
        assertThat(listing.getBuyerId()).isEqualTo(200L);
        assertThat(listing.getReserverId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("데이터베이스 연동: 삭제된 글은 활성 조회에서 제외된다")
    void removedListingIsInvisible() {
        Long listingId = listingCommandService.createDraft(100L,1);

        listingCommandService.remove(100L, listingId);

        assertThat(listingJpaRepository.findByIdAndDeletedAtIsNull(listingId)).isEmpty();
        assertThatThrownBy(() -> listingQueryService.getListing(listingId))
                .isInstanceOf(ListingNotFoundException.class);
    }
}
