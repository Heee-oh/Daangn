package com.daangn.market.Listing;

import com.daangn.market.Listing.domain.HopeLocation;
import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.domain.Status;
import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.RegionId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ListingTest {

    @Test
    @DisplayName("초기 작성 Draft 체크 (정상 판매글)")
    void beforePublish1() {
        Listing listing = Listing.draftPrice(10L, false);
        Assertions.assertThat(listing.getStatus()).isSameAs(Status.DRAFT);
        Assertions.assertThat(listing.getPrice().isFree()).isFalse();
        Assertions.assertThat(listing.isHidden()).isFalse();

    }

    @Test
    @DisplayName("초기 작성 Draft 체크 (무료 나눔)")
    void beforePublish2() {
        Listing listing = Listing.draftPrice(0L, true);
        Assertions.assertThat(listing.getStatus()).isSameAs(Status.DRAFT);
        Assertions.assertThat(listing.getPrice().isFree()).isTrue();
        Assertions.assertThat(listing.isHidden()).isFalse();
    }

    @Test
    @DisplayName("판매글 정상 가격 publish 성공")
    void publishSuccess1() {
        // 무료가 아닌 1이상의 정상 가격
        Listing listing = Listing.draftPrice(10L, false);
        ;
        listing.publish();

        Assertions.assertThat(listing.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);

    }

    @Test
    @DisplayName("무료이며 가격이 null 일 경우")
    void publishSuccess2() {
        Listing listing2 = Listing.draftPrice(null, true);
        listing2.publish();

        Assertions.assertThat(listing2.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);

        Assertions.assertThat(listing2.getPrice().getPriceAmount()).isEqualTo(0L);

    }

    @Test
    @DisplayName("무료이면서 0원일 경우")
    void publishSuccess3() {
        Listing listing3 = Listing.draftPrice(0L, true);
        ;
        listing3.publish();

        Assertions.assertThat(listing3.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);
    }

    @Test
    @DisplayName("무료인데 가격이 1이상 존재")
    void publishSuccess4() {
        Listing listing4 = Listing.draftPrice(40L, true);
        ;
        listing4.publish();

        Assertions.assertThat(listing4.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);

        Assertions.assertThat(listing4.getPrice().getPriceAmount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Publish는 1번만 실행")
    void publishOne() {
        Listing listing = Listing.draftPrice(10L, false);
        listing.publish();


        Assertions.assertThat(listing.getStatus()).isSameAs(Status.PUBLISHED);

        // 1번 더 실행시 예외 발생
        Assertions.assertThatThrownBy(() -> listing.publish())
                .isInstanceOf(IllegalStateException.class);

    }


    @Test
    @DisplayName("hide로 숨김")
    void hide() {
        Listing listing = Listing.draft();

        Assertions.assertThatThrownBy(() -> listing.hide())
                .as("초안은 숨김 불가능")
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThatThrownBy(() -> listing.unHide())
                .as("이미 노출 상태")
                .isInstanceOf(IllegalStateException.class);


        listing.publish();
        listing.updatePrice(100L, true);


        listing.hide();
        Assertions.assertThat(listing.isHidden()).isTrue();
        listing.unHide();
        Assertions.assertThat(listing.isHidden()).isFalse();


        listing.reserve(new MemberId(1234L));
        listing.hide();
        Assertions.assertThat(listing.isHidden()).isTrue();
        listing.unHide();
        Assertions.assertThat(listing.isHidden()).isFalse();

        listing.markSoldOut(new MemberId(1234L));
        listing.hide();
        Assertions.assertThat(listing.isHidden()).isTrue();
        listing.unHide();
        Assertions.assertThat(listing.isHidden()).isFalse();

        // 삭제후 예외 던짐
        Listing listing2 = Listing.draft();
        listing2.remove();
        Assertions.assertThatThrownBy(() -> listing2.hide())
                .as("삭제된 게시글")
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThatThrownBy(() -> listing2.unHide())
                .as("삭제된 게시글")
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("예약 각종 실패 테스트")
    void reserveFailTest() {
        Listing listing = Listing.draft();

        Assertions.assertThatThrownBy(() -> listing.reserve(null))
                .as("Member null")
                .isInstanceOf(IllegalArgumentException.class);


        Assertions.assertThatThrownBy(() -> listing.reserve(new MemberId(1234L)))
                .as("예약 불가 상태 (초안)")
                .isInstanceOf(IllegalStateException.class);

        // 예약 취소 실패
        Assertions.assertThatThrownBy(() -> listing.cancelReserve())
                .as("예약 상태 아님")
                .isInstanceOf(IllegalStateException.class);

        listing.publish();
        listing.hide();
        Assertions.assertThatThrownBy(() -> listing.reserve(new MemberId(1234L)))
                .as("숨김 상태")
                .isInstanceOf(IllegalStateException.class);

        listing.remove();
        Assertions.assertThatThrownBy(() -> listing.reserve(new MemberId(1234L)))
                .as("삭제된 게시글")
                .isInstanceOf(IllegalStateException.class);

        // 예약 취소 실패 (삭제됨)
        Assertions.assertThatThrownBy(() -> listing.cancelReserve())
                .as("삭제된 게시글")
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    @DisplayName("예약 후 취소 성공 테스트")
    void reserveSuccessTest() {
        Listing listing = Listing.draft();
        listing.publish();

        listing.reserve(new MemberId(1234L));

        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.RESERVED);
        Assertions.assertThat(listing.getReserverId().getValue()).isEqualTo(1234L);

        listing.cancelReserve();
        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.PUBLISHED);
        Assertions.assertThat(listing.getReserverId()).isNull();
    }

    @Test
    @DisplayName("soldout에서 실패 테스트")
    void soldOutFail() {
        Listing listing = Listing.draft();
        listing.publish();


        Assertions.assertThatThrownBy(() -> listing.markSoldOut(null))
                .as("예약 상태가 아님")
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThatThrownBy(() -> listing.markSoldOut(new MemberId(1234L)))
                .as("예약 상태 아님")
                .isInstanceOf(IllegalStateException.class);

        listing.reserve(new MemberId(123L));

        Assertions.assertThatThrownBy(() -> listing.markSoldOut(new MemberId(1234L)))
                .as("잘못된 구매자 id")
                .isInstanceOf(IllegalArgumentException.class);

        listing.cancelReserve();
        listing.remove();

        Assertions.assertThatThrownBy(() -> listing.markSoldOut(new MemberId(1234L)))
                .as("삭제된 게시글")
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    @DisplayName("soldOut 성공 테스트")
    void soldOutSuccess() {
        Listing listing = Listing.draft();
        listing.publish();

        listing.reserve(new MemberId(123L));
        listing.markSoldOut(new MemberId(123L));
        Assertions.assertThat(listing.getStatus()).isEqualTo(Status.SOLD_OUT);
        Assertions.assertThat(listing.getBuyerId().getValue()).isEqualTo(123L);

    }

    @Test
    @DisplayName("remove 실패 테스트")
    void removeFailTest() {
        Listing listing = Listing.draft();
        listing.publish();
        listing.reserve(new MemberId(1L));
        Assertions.assertThatThrownBy(() -> listing.remove())
                .as("예약하고 삭제 시도")
                .isInstanceOf(IllegalStateException.class);


        listing.cancelReserve();
        listing.remove();
        Assertions.assertThatThrownBy(() -> listing.remove())
                .as("삭제 2번")
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    @DisplayName("update 실패 테스트")
    void updateFailTest() {
        Listing listing = Listing.draft();
        Assertions.assertThatThrownBy(() -> listing.updatePrice(-220L, false))
                .as("0 이하 값 설정")
                .isInstanceOf(IllegalArgumentException.class);

        listing.publish();

        listing.reserve(new MemberId(1234L));
        Assertions.assertThatThrownBy(() -> listing.updatePrice(0L, true))
                .as("예약중 값 변경")
                .isInstanceOf(IllegalStateException.class);

        listing.cancelReserve();
        listing.remove();
        Assertions.assertThatThrownBy(() -> listing.updatePrice(0L, true))
                .as("삭제된 게시글")
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("update 가격 성공 테스트")
    void updatePriceSuccessTest() {
        Listing listing = Listing.draft();
        listing.updatePrice(100L, false);
        Assertions.assertThat(listing.getPrice()).isNotNull();
        Assertions.assertThat(listing.getPrice().getPriceAmount()).isEqualTo(100L);
        Assertions.assertThat(listing.getPrice().isFree()).isFalse();

        // 값이 있어도 isFree가 true면 0원 설정
        Listing listing2 = Listing.draft();
        listing2.updatePrice(100L, true);
        Assertions.assertThat(listing2.getPrice()).isNotNull();
        Assertions.assertThat(listing2.getPrice().getPriceAmount()).isEqualTo(0L);
        Assertions.assertThat(listing2.getPrice().isFree()).isTrue();
    }

    @Test
    @DisplayName("update 제목, 설명 성공 테스트")
    void updateTitleAndDescriptionSuccessTest() {
        Listing listing = Listing.draft();
        listing.updateTitleAndDescription(null, "abc");

        Assertions.assertThat(listing.getTitle()).isNull();
        Assertions.assertThat(listing.getDescription()).isEqualTo("abc");


        listing.updateTitleAndDescription("title", "abc");
        Assertions.assertThat(listing.getTitle()).isEqualTo("title");
        Assertions.assertThat(listing.getDescription()).isEqualTo("abc");

        listing.updateTitleAndDescription("title", null);
        Assertions.assertThat(listing.getTitle()).isEqualTo("title");
        Assertions.assertThat(listing.getDescription()).isEqualTo("abc");

    }

}