package com.danggeun.market.service;

import com.danggeun.market.domain.Member;
import com.danggeun.market.dto.MemberSignDto;
import com.danggeun.market.repository.member.MemberQueryDslRepository;
import com.danggeun.market.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryDslRepository memberQueryDslRepository;


    public Member savePost(MemberSignDto memberSignDto) {

        Member member = Member.builder()
                .nickname(memberSignDto.nickname())
                .name(memberSignDto.name())
                .phoneNumber(memberSignDto.phoneNumber())
                .build();

        return memberRepository.save(member);
    }

    public Member findMember(Long id) {
        if (id == null) throw new IllegalArgumentException("Member id is null");
        return memberRepository
                .findMemberById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원"));
    }

}
