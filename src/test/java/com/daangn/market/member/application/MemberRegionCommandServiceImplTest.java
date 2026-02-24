package com.daangn.market.member.application;

import com.daangn.market.member.domain.MemberRegion;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionNotFoundException;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionVerificationFailedException;
import com.daangn.market.member.infrastructure.memberRegion.MemberRegionJpaRepository;
import com.daangn.market.region.infrastructure.RegionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberRegionCommandServiceImplTest {
    @Mock
    MemberRegionJpaRepository memberRegionJpaRepository;
    @Mock
    RegionJpaRepository regionJpaRepository;

    @InjectMocks
    MemberRegionCommandServiceImpl service;

    @Test
    @DisplayName("본인 memberRegion이 존재하고 좌표가 해당 region에 포함되면 인증 시간을 갱신한다")
    void verifyMemberRegion_updatesVerifiedAt_whenCoordinateIsInRegion() {
        // given
        Long memberRegionId = 1L;
        Long memberId = 10L;
        double lat = 37.5665;
        double lng = 126.9780;

        MemberRegion memberRegion = mock(MemberRegion.class);
        when(memberRegion.getRegionId()).thenReturn(20);

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.of(memberRegion));

        when(regionJpaRepository.validateCoordinateInRegion(20, lat, lng))
                .thenReturn(true);

        // when
        service.verifyMemberRegion(memberRegionId, memberId, lat, lng);

        // then
        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verify(regionJpaRepository).validateCoordinateInRegion(20, lat, lng);

        // Instant.now()는 고정값 비교가 어려우니 "호출됐는지"만 검증
        verify(memberRegion).verify(any(Instant.class));
        verifyNoMoreInteractions(regionJpaRepository);
    }

    @Test
    @DisplayName("memberRegion이 없으면 MemberRegionNotFoundException을 던진다")
    void verifyMemberRegion_throwsNotFound_whenMemberRegionDoesNotExist() {
        // given
        Long memberRegionId = 1L;
        Long memberId = 10L;

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                service.verifyMemberRegion(memberRegionId, memberId, 37.0, 127.0)
        ).isInstanceOf(MemberRegionNotFoundException.class);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verifyNoInteractions(regionJpaRepository);  // jpa는 아무 호출도 일어나지 않음 검증
    }

    @Test
    @DisplayName("memberRegion의 regionId가 null이면 MemberRegionNotFoundException을 던진다")
    void verifyMemberRegion_throwsIllegalState_whenRegionIdIsNull() {
        // given
        Long memberRegionId = 1L;
        Long memberId = 10L;

        MemberRegion memberRegion = mock(MemberRegion.class);
        when(memberRegion.getRegionId()).thenReturn(null);

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.of(memberRegion));

        // when & then
        assertThatThrownBy(() ->
                service.verifyMemberRegion(memberRegionId, memberId, 37.0, 127.0)
        ).isInstanceOf(MemberRegionNotFoundException.class);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verifyNoInteractions(regionJpaRepository);
        verify(memberRegion, never()).verify(any());
    }

    @Test
    @DisplayName("좌표가 region에 포함되지 않으면 MemberRegionVerificationFailedException을 던진다")
    void verifyMemberRegion_throwsVerificationFailed_whenCoordinateIsNotInRegion() {
        // given
        Long memberRegionId = 1L;
        Long memberId = 10L;
        double lat = 35.1796;
        double lng = 129.0756;

        MemberRegion memberRegion = mock(MemberRegion.class);
        when(memberRegion.getRegionId()).thenReturn(20);

        when(memberRegionJpaRepository.findMemberRegionByIdAndMemberId(memberRegionId, memberId))
                .thenReturn(Optional.of(memberRegion));

        when(regionJpaRepository.validateCoordinateInRegion(20, lat, lng))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                service.verifyMemberRegion(memberRegionId, memberId, lat, lng)
        ).isInstanceOf(MemberRegionVerificationFailedException.class);

        verify(memberRegionJpaRepository).findMemberRegionByIdAndMemberId(memberRegionId, memberId);
        verify(regionJpaRepository).validateCoordinateInRegion(20, lat, lng);
        verify(memberRegion, never()).verify(any());
    }


}