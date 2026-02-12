package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.id.RegionId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HopeLocationTest {


    @Test
    @DisplayName("희망 거래 장소 테스트")
    void hopeLocationTest() {

        //  region id null
        Assertions.assertThatThrownBy(() -> new HopeLocation(null, 33.3, 127.3))
                .isInstanceOf(IllegalArgumentException.class);

        // lat log 순서 바뀜
        Assertions.assertThatThrownBy(() -> new HopeLocation(new RegionId(123), 127, 33.3))
                .isInstanceOf(IllegalArgumentException.class);

        // lat log 경계값
        Assertions.assertThatThrownBy(() -> new HopeLocation(new RegionId(123), -90, 181))
                .isInstanceOf(IllegalArgumentException.class);



    }

}