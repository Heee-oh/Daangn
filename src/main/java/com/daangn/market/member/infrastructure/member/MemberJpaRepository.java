package com.daangn.market.member.infrastructure.member;

import com.daangn.market.member.domain.Member;
import com.daangn.market.member.domain.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByPhoneNumber(PhoneNumber phoneNumber);
}
