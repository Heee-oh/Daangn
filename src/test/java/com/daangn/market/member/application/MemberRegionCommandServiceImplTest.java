package com.daangn.market.member.application;

import com.daangn.market.member.domain.MemberRegion;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionNotFoundException;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionVerificationFailedException;
import com.daangn.market.member.infrastructure.member.MemberJpaRepository;
import com.daangn.market.member.infrastructure.memberRegion.MemberRegionJpaRepository;
import com.daangn.market.region.infrastructure.RegionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberRegionCommandServiceImplTest {

    @Mock
    MemberJpaRepository memberJpaRepository;

    @Mock
    MemberRegionJpaRepository memberRegionJpaRepository;

    @Mock
    RegionJpaRepository regionJpaRepository;

    @InjectMocks
    MemberRegionCommandServiceImpl service;

    @Test
    @DisplayName("본인 memberRegion이 존재하고 좌표가 해당 region에 포함되면 인증 시간을 갱신한다")
    void verifyMemberRegion_updatesVerifiedAt_whenCoordinateIsInRegion() {
        Long memberRegionId = 1L;
        Long memberId = 10L;
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lng = new BigDecimal("126.9780");

        MemberRegion memberRegion = org.mockito.Mockito.mock(MemberRegion.class);
        when(memberRegion.getRegionId()).thenReturn(20);

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.of(memberRegion));

        when(regionJpaRepository.validateCoordinateInRegion(20, lat, lng))
                .thenReturn(true);

        service.verifyMemberRegion(memberRegionId, memberId, lat, lng);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verify(regionJpaRepository).validateCoordinateInRegion(20, lat, lng);

        verify(memberRegion).verify(any(Instant.class));
        verifyNoMoreInteractions(regionJpaRepository);
    }

    @Test
    @DisplayName("memberRegion이 없으면 MemberRegionNotFoundException을 던진다")
    void verifyMemberRegion_throwsNotFound_whenMemberRegionDoesNotExist() {
        Long memberRegionId = 1L;
        Long memberId = 10L;

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.verifyMemberRegion(memberRegionId, memberId, new BigDecimal("37.0"), new BigDecimal("127.0"))
        ).isInstanceOf(MemberRegionNotFoundException.class);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verifyNoInteractions(regionJpaRepository);
    }

    @Test
    @DisplayName("memberRegion의 regionId가 null이면 MemberRegionNotFoundException을 던진다")
    void verifyMemberRegion_throwsNotFound_whenRegionIdIsNull() {
        Long memberRegionId = 1L;
        Long memberId = 10L;

        MemberRegion memberRegion = org.mockito.Mockito.mock(MemberRegion.class);
        when(memberRegion.getRegionId()).thenReturn(null);

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.of(memberRegion));

        assertThatThrownBy(() ->
                service.verifyMemberRegion(memberRegionId, memberId, new BigDecimal("37.0"), new BigDecimal("127.0"))
        ).isInstanceOf(MemberRegionNotFoundException.class);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verifyNoInteractions(regionJpaRepository);
        verify(memberRegion, never()).verify(any());
    }

    @Test
    @DisplayName("좌표가 region에 포함되지 않으면 MemberRegionVerificationFailedException을 던진다")
    void verifyMemberRegion_throwsVerificationFailed_whenCoordinateIsNotInRegion() {
        Long memberRegionId = 1L;
        Long memberId = 10L;
        BigDecimal lat = new BigDecimal("35.1796");
        BigDecimal lng = new BigDecimal("129.0756");

        MemberRegion memberRegion = org.mockito.Mockito.mock(MemberRegion.class);
        when(memberRegion.getRegionId()).thenReturn(20);

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.of(memberRegion));

        when(regionJpaRepository.validateCoordinateInRegion(20, lat, lng))
                .thenReturn(false);

        assertThatThrownBy(() ->
                service.verifyMemberRegion(memberRegionId, memberId, lat, lng)
        ).isInstanceOf(MemberRegionVerificationFailedException.class);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verify(regionJpaRepository).validateCoordinateInRegion(20, lat, lng);
        verify(memberRegion, never()).verify(any());
    }
}