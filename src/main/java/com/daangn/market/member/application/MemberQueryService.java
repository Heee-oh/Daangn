package com.daangn.market.member.application;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.InterestResponse;
import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MemberQueryService {
    MemberResponse getMe(MemberId memberId);
    // 내 활동 지역 목록 조회
    List<MemberRegionResponse> getMyRegions(MemberId memberId);

    // 내 관심 목록 조회
    Slice<InterestResponse> getMyInterests(MemberId memberId, int page, int size);
}
