package com.daangn.market.member.infrastructure.memberRegion;

import com.daangn.market.member.domain.MemberRegion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MemberRegionJpaRepository extends JpaRepository<MemberRegion, Long>, MemberRegionRepositoryCustom {

    Optional<MemberRegion> findMemberRegionByIdAndMemberId(Long id, Long memberId);

    boolean existsByMember_IdAndRegionId(Long memberId, Integer regionId);

    long countByMember_Id(Long memberId);

    void deleteByMember_Id(Long memberId);
}
