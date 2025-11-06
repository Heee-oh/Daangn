package com.danggeun.market.service;

import com.danggeun.market.domain.Member;
import com.danggeun.market.dto.MemberSignDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService service;

    @Test
    @Transactional
    void saveAndFind() {
        Member member = service.savePost(new MemberSignDto("abc", "aaa", "010-2222-2222"));
        Member member1 = service.findMember(member.getId());

        log.info("tag = {}", member.getTag());
        Assertions.assertThat(member.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(member1.getTag()).isEqualTo(member.getTag());
        org.junit.jupiter.api.Assertions.assertSame(member, member1);

    }

}