package com.daangn.market.member.infrastructure.memberRegion;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.domain.MemberRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MemberRegionJpaRepository extends JpaRepository<MemberRegion, Long> {

    @Query(
            """ 
               SELECT mr.MemberRegionId, mr.regionId, mr.verifiedAt, mr.primary, r.dongnm 
               FROM MemberRegion mr
               INNER JOIN Region r ON mr.regionId = r.id
               WHERE mr.member.memberId = :memberId 
            """)
    List<MemberRegionResponse> findAllByMember(MemberId memberId);

}
