package com.daangn.market.service;

import com.daangn.market.domain.Member;
import com.daangn.market.dto.request.MemberSignDto;
import com.daangn.market.repository.member.MemberQueryDslRepository;
import com.daangn.market.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryDslRepository memberQueryDslRepository;

    @Transactional
    public Member saveMember(MemberSignDto memberSignDto) {
        // 멤버 생성
        Member member = Member.builder()
                .nickname(memberSignDto.nickname())
                .name(memberSignDto.name())
                .phoneNumber(memberSignDto.phoneNumber())
                .build();

        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findMember(Long id) {
        if (id == null) throw new IllegalArgumentException("Member id is null");
        return memberRepository
                .findMemberById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원"));
    }

}
