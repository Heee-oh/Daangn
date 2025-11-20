package com.daangn.market.repository.member;

import com.daangn.market.domain.Member;
import com.daangn.market.domain.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 특정 유저 조회
    Optional<Member> findMemberById(Long id);

    boolean existsMemberById(Long id);

    // 특정 상태의 회원 전부 조회
    Page<Member> findMemberByIdAndStatusIs(Long id, MemberStatus status, Pageable pageable);

    // 비활 상태 1달이상 유지된 멤버들 다 삭제 처리
    void deleteAllByStatusAndLastModifiedAtBefore(MemberStatus status, LocalDateTime cutoff);

    // 특정 여러 상태 카운트
    long countByStatusIn(MemberStatus... statuses);
    // 특정 상태가 아닌 나머지 카운트
    long countByStatusNotIn(MemberStatus... statuses);


}
