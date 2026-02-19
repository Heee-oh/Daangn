package com.daangn.market.member.infrastructure.Interest;

import com.daangn.market.common.domain.id.InterestId;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface InterestJpaRepository extends JpaRepository<Interest, InterestId>, InterestRepositoryCustom {

    @Modifying
    @Query("DELETE FROM Interest i WHERE i.listingId = :listingId AND i.member = :memberId")
    int deleteByListingIdAndMemberId(
            @Param("listingId") ListingId listingId,
            @Param("memberId") MemberId memberId
    );

    @Transactional(readOnly = true)
    @Query("SELECT COUNT(i) > 0 FROM Interest i WHERE i.listingId = :listingId AND i.member.memberId = :memberId")
    boolean existsByListingIdAndMemberId(
            @Param("listingId") ListingId listingId,
            @Param("memberId") MemberId memberId
    );


}
