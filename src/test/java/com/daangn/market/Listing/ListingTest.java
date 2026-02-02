package com.daangn.market.Listing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ListingTest {

    @Test
    @DisplayName("초기 작성 Draft 체크 (정상 판매글)")
    void beforePublish1() {
        Listing listing = new Listing(10L, false);
        Assertions.assertThat(listing.getStatus()).isSameAs(Status.DRAFT);
        Assertions.assertThat(listing.isFree()).isFalse();
        Assertions.assertThat(listing.isHidden()).isFalse();
    }

    @Test
    @DisplayName("초기 작성 Draft 체크 (무료 나눔)")
    void beforePublish2() {
        Listing listing = new Listing(0L, true);
        Assertions.assertThat(listing.getStatus()).isSameAs(Status.DRAFT);
        Assertions.assertThat(listing.isFree()).isTrue();
        Assertions.assertThat(listing.isHidden()).isFalse();
    }

    @Test
    @DisplayName("판매글 정상 가격 publish 성공")
    void publishSuccess1() {
        // 무료가 아닌 1이상의 정상 가격
        Listing listing = new Listing(10L, false);
        listing.publish();

        Assertions.assertThat(listing.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);

    }
    @Test
    @DisplayName( "무료이며 가격이 null 일 경우")
    void publishSuccess2() {
        Listing listing2 = new Listing(null, true);
        listing2.publish();

        Assertions.assertThat(listing2.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);

        Assertions.assertThat(listing2.getPrice()).isEqualTo(0L);

    }

    @Test
    @DisplayName("무료이면서 0원일 경우")
    void publishSuccess3() {
        Listing listing3 = new Listing(0L, true);
        listing3.publish();

        Assertions.assertThat(listing3.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);



    }

    @Test
    @DisplayName("무료인데 가격이 1이상 존재")
    void publishSuccess4() {
        Listing listing4 = new Listing(40L, true);
        listing4.publish();

        Assertions.assertThat(listing4.getStatus())
                .as("리스팅 발행 후 상태 검증")
                .isSameAs(Status.PUBLISHED);

        Assertions.assertThat(listing4.getPrice()).isEqualTo(0L);
    }

    @Test
    @DisplayName("무료가 아닌데 가격이 NULL publish 실패 검증")
    void publishFail() {
        Listing listing = new Listing(null, false);

        Assertions.assertThatThrownBy(() -> listing.publish())
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("무료가 아니지만 0원일 경우 publish 실패 검증")
    void publishFail2() {
        Listing listing2 = new Listing(0L, false);

        Assertions.assertThatThrownBy(() -> listing2.publish())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Publish는 1번만 실행")
    void publishOne() {
        Listing listing = new Listing(10L, false);
        listing.publish();


        Assertions.assertThat(listing.getStatus()).isSameAs(Status.PUBLISHED);

        // 1번 더 실행시 예외 발생
        Assertions.assertThatThrownBy(() -> listing.publish())
                .isInstanceOf(IllegalStateException.class);

    }




}