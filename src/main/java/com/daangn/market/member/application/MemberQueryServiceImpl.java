package com.daangn.market.member.application;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.InterestResponse;
import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;
import com.daangn.market.member.infrastructure.Interest.InterestJpaRepository;
import com.daangn.market.member.infrastructure.member.MemberQueryDslRepository;
import com.daangn.market.member.infrastructure.memberRegion.MemberRegionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService{

    private final MemberQueryDslRepository memberQueryDslRepository;
    private final MemberRegionJpaRepository memberRegionJpaRepository;
    private final InterestJpaRepository interestJpaRepository;

    @Override
    public MemberResponse getMe(MemberId memberId) {
        return memberQueryDslRepository.findMember(memberId);
    }

    @Override
    public List<MemberRegionResponse> getMyRegions(MemberId memberId) {
        List<MemberRegionResponse> memberRegions = memberRegionJpaRepository.findAllByMember(memberId);
        return memberRegions;
    }

    // TODO 이어서 진행
    @Override
    public Slice<InterestResponse> getMyInterests(MemberId memberId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("memberRegionId").descending());


        return null;
    }
}
