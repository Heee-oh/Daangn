package com.daangn.market.member.infrastructure.Interest;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.InterestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface InterestRepositoryCustom {
    Slice<InterestResponse> findAllByMemberId(MemberId memberId, Pageable pageable);
}
