package com.daangn.market.member.application;

import com.daangn.market.member.domain.MemberRegion;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionNotFoundException;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionVerificationFailedException;
import com.daangn.market.member.infrastructure.memberRegion.MemberRegionJpaRepository;
import com.daangn.market.region.infrastructure.RegionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberRegionCommandServiceImpl implements MemberRegionCommandService {

    private final MemberRegionJpaRepository memberRegionJpaRepository;
    private final RegionJpaRepository regionJpaRepository;

    @Override
    public void verifyMemberRegion(Long memberRegionId, Long memberId, double lat, double lng) {
        MemberRegion memberRegion = memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId)
                .orElseThrow(MemberRegionNotFoundException::new);


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
