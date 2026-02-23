package com.daangn.market.member.infrastructure.Interest;

import com.daangn.market.member.application.dto.InterestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface InterestRepositoryCustom {
    Slice<InterestResponse> findAllByMemberId(Long memberId, Long lastInterestId, Pageable pageable);
}
