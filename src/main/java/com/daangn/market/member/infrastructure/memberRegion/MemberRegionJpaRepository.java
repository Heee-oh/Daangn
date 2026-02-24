package com.daangn.market.member.infrastructure.memberRegion;

import com.daangn.market.member.domain.MemberRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface MemberRegionJpaRepository extends JpaRepository<MemberRegion, Long>, MemberRegionRepositoryCustom {

    Optional<MemberRegion> findMemberRegionByIdAndMemberId(Long id, Long MemberId);
}
