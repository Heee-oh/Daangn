package com.daangn.market.member.infrastructure.member;

import com.daangn.market.member.application.dto.MemberResponse;
import com.daangn.market.member.domain.Member;
import com.daangn.market.member.domain.PhoneNumber;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberRepositoryCustomImplTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @Transactional
    void qTest() {
        Member saved = memberJpaRepository.save(new Member("abc", new PhoneNumber("01012345678")));
        MemberResponse member = memberJpaRepository.findMember(saved.getId());

        Assertions.assertThat(member).isNotNull();
        Assertions.assertThat(member.mannerTemp()).isEqualTo(365);
        Assertions.assertThat(member.nickname()).isEqualTo("abc");
    }
}