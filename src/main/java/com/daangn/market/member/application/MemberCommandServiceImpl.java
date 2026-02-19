package com.daangn.market.member.application;

import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.MemberSignupCommand;
import com.daangn.market.member.application.dto.MemberUpdateCommand;
import com.daangn.market.member.domain.Interest;
import com.daangn.market.member.domain.Member;
import com.daangn.market.member.infrastructure.Interest.InterestJpaRepository;
import com.daangn.market.member.infrastructure.member.MemberJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService{

    private final MemberJpaRepository memberJpaRepository;
    private final InterestJpaRepository interestJpaRepository;

    @Override
    public MemberId signup(MemberSignupCommand command) {
        if (memberJpaRepository.findByPhoneNumber(command.phoneNumber()).isPresent()) {
            throw new RuntimeException("이미 존재하는 회원");
        }

        Member member = new Member(command.nickname(), command.phoneNumber());
        Member saved = memberJpaRepository.save(member);
        return saved.getMemberId();
    }

    @Override
    public void updateNickname(MemberId memberId,String nickname) {
        Objects.requireNonNull(nickname, "닉네임은 필수입니다.");

        Member member = findMemberById(memberId);

        member.updateNickname(nickname);
    }

    @Override
    public void updateMemberInfo(MemberId memberId, MemberUpdateCommand command) {
        Member member = findMemberById(memberId);

        // null이 아닐 때만 업데이트 수행
        Optional.ofNullable(command.nickname()).ifPresent(member::updateNickname);
        Optional.ofNullable(command.profileImage()).ifPresent(member::updateProfileImage);
    }

    @Override
    public void updateProfileImage(MemberId memberId, String profileImage) {
        Objects.requireNonNull(profileImage, "프로필 이미지 경로는 필수입니다.");

        Member member = findMemberById(memberId);

        member.updateProfileImage(profileImage);
    }

    @Override
    public void addInterest(MemberId memberId, ListingId listingId) {
        Objects.requireNonNull(listingId, "listingId는 필수입니다.");

        if (interestJpaRepository.existsByListingIdAndMemberId(listingId, memberId)) {
            log.info("이미 존재하는 관심 판매글");
            return;
        }

        Member member = findMemberById(memberId);

        member.addInterest(new Interest(listingId));
    }

    @Override
    public void deleteInterest(MemberId memberId, ListingId listingId) {
        Objects.requireNonNull(listingId, "listingId는 필수입니다.");

        int result = interestJpaRepository.deleteByListingIdAndMemberId(listingId, memberId);

        if (result == 0) {
            log.info("이미 삭제된 관심 항목입니다. MemberId: {}, ListingId: {}", memberId, listingId);
        }
    }

    @Override
    public void withdraw(MemberId memberId) {
        Member member = findMemberById(memberId);

        member.withdraw();

    }


    // methods
    private Member findMemberById(MemberId memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾지 못함"));
    }
}
