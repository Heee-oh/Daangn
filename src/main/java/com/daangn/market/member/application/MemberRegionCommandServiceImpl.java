package com.daangn.market.member.application;

import com.daangn.market.member.domain.Member;
import com.daangn.market.member.domain.MemberRegion;
import com.daangn.market.member.domain.exception.MemberNotFoundException;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionNotFoundException;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionVerificationFailedException;
import com.daangn.market.member.infrastructure.member.MemberJpaRepository;
import com.daangn.market.member.infrastructure.memberRegion.MemberRegionJpaRepository;
import com.daangn.market.region.infrastructure.RegionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberRegionCommandServiceImpl implements MemberRegionCommandService {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRegionJpaRepository memberRegionJpaRepository;
    private final RegionJpaRepository regionJpaRepository;

    @Override
    public void verifyMemberRegion(Long memberRegionId, Long memberId, BigDecimal lat, BigDecimal lng) {
        MemberRegion memberRegion = memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId)
                .orElseThrow(MemberRegionNotFoundException::new);

        if (memberRegion.getRegionId() == null) {
            throw new MemberRegionNotFoundException();
        }

        verify(memberRegion, lat, lng);
    }

    @Override
    public void verifyMemberRegionByRegionId(Integer regionId, Long memberId, BigDecimal lat, BigDecimal lng) {
        if (!regionJpaRepository.validateCoordinateInRegion(regionId, lat, lng)) {
            throw new MemberRegionVerificationFailedException();
        }

        Member member = memberJpaRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.addRegion(new MemberRegion(regionId, true));
    }

    private void verify(MemberRegion memberRegion, BigDecimal lat, BigDecimal lng) {
        Integer regionId = memberRegion.getRegionId();

        if (regionId == null) {
            throw new MemberRegionNotFoundException();
        }

        if (!regionJpaRepository.validateCoordinateInRegion(regionId, lat, lng)) {
            throw new MemberRegionVerificationFailedException();
        }

        memberRegion.verify(Instant.now());
    }
}