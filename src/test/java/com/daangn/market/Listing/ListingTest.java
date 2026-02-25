package com.daangn.market.Listing;

import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.domain.Status;
import com.daangn.market.Listing.exception.ListingBadRequestException;
import com.daangn.market.Listing.exception.ListingConflictException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ListingTest {

    @Test
    @DisplayName("초안 생성 시 기본 상태값을 가진다")
    void draftDefaultState() {
        Listing listing = Listing.draftPrice(10L, false);

        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.DRAFT);
        Assertions.assertThat(listing.isHidden()).isFalse();
        Assertions.assertThat(listing.getPrice().getPriceAmount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("게시하면 상태가 초안에서 게시중으로 변경된다")
    void publishSuccess() {
        Listing listing = Listing.draftPrice(10L, false);

        listing.publish();

        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.PUBLISHED);
    }

    @Test
    @DisplayName("초안이 아닌 상태에서 게시하면 충돌 예외가 발생한다")
    void publishFailWhenNotDraft() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();

        Assertions.assertThatThrownBy(listing::publish)
                .isInstanceOf(ListingConflictException.class);
    }

    @Test
    @DisplayName("게시된 글은 숨김과 숨김 해제를 수행할 수 있다")
    void hideUnhideSuccess() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();

        listing.hide();
        Assertions.assertThat(listing.isHidden()).isTrue();

        listing.unHide();
        Assertions.assertThat(listing.isHidden()).isFalse();
    }

    @Test
    @DisplayName("예약자 아이디가 비어 있으면 예약에 실패한다")
    void reserveFailWhenBuyerNull() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();

        Assertions.assertThatThrownBy(() -> listing.reserve(null))
                .isInstanceOf(ListingBadRequestException.class);
    }

    @Test
    @DisplayName("예약 후 예약 취소 시 상태가 정상 전이된다")
    void reserveAndCancelSuccess() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();

        listing.reserve(123L);
        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.RESERVED);
        Assertions.assertThat(listing.getReserverId()).isEqualTo(123L);

        listing.cancelReserve();
        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.PUBLISHED);
        Assertions.assertThat(listing.getReserverId()).isNull();
    }

    @Test
    @DisplayName("판매 완료는 예약된 구매자만 확정할 수 있다")
    void soldOutValidation() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();
        listing.reserve(123L);

        Assertions.assertThatThrownBy(() -> listing.markSoldOut(456L))
                .isInstanceOf(ListingBadRequestException.class);

        listing.markSoldOut(123L);
        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.SOLD_OUT);
        Assertions.assertThat(listing.getBuyerId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("가격 입력이 유효하지 않으면 가격 수정에 실패한다")
    void updatePriceInvalid() {
        Listing listing = Listing.draft();

        Assertions.assertThatThrownBy(() -> listing.updatePrice(0L, false))
                .isInstanceOf(ListingBadRequestException.class);
    }

    @Test
    @DisplayName("예약 상태에서는 삭제를 수행할 수 없다")
    void removeFailWhenReserved() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();
        listing.reserve(1L);

        Assertions.assertThatThrownBy(listing::remove)
                .isInstanceOf(ListingConflictException.class);
    }
}
