package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.common.domain.id.RegionId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberRegionTest {

    @Test
    void verifyRegionTest() {
        MemberRegion memberRegion = new MemberRegion(new RegionId(123), true);


    }
}