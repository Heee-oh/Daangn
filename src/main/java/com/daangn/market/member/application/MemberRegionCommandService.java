package com.daangn.market.member.application;

public interface MemberRegionCommandService {
    /**
     * 내 동네정보를 가져와서 현재 위치 값이 설정한 동네의 범위 안에 포함되어있는지 확인 후 인증 갱신
     * @param memberRegionId
     */
    void verifyMemberRegion(Long memberRegionId, Long memberId, double lat, double lng);
}
