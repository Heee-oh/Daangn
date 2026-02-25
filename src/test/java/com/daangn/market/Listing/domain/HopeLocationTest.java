package com.daangn.market.Listing.domain;

import com.daangn.market.Listing.exception.ListingBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HopeLocationTest {

    @Test
    @DisplayName("희망 거래 위치는 지역과 좌표 유효성을 검증한다")
    void hopeLocationValidation() {
        Assertions.assertThatThrownBy(() -> new HopeLocation(null, 33.3, 127.3))
                .isInstanceOf(ListingBadRequestException.class);

        Assertions.assertThatThrownBy(() -> new HopeLocation(123, 127, 33.3))
                .isInstanceOf(ListingBadRequestException.class);

        Assertions.assertThatThrownBy(() -> new HopeLocation(123, -90, 181))
                .isInstanceOf(ListingBadRequestException.class);
    }
}
