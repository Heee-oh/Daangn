package com.daangn.market.member.application;

import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.MemberSignupCommand;
import com.daangn.market.member.application.dto.MemberUpdateCommand;

public interface MemberCommandService {
    // 회원 가입
    MemberId signup(MemberSignupCommand command);

    // 닉네임 수정
    void updateNickname(MemberId memberId, String nickname);

    // 내 정보 수정 (복합 수정)
    void updateMemberInfo(MemberId memberId, MemberUpdateCommand command);
    // 프로필 이미지 수정
    void updateProfileImage(MemberId memberId, String profileImage);

    // 관심 목록 추가
    void addInterest(MemberId memberId, ListingId listingId);

    // 관심 목록 삭제
    void deleteInterest(MemberId memberId, ListingId listingId);

    // 회원 탈퇴
    void withdraw(MemberId memberId);
}