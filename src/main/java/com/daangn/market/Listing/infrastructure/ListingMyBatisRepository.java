package com.daangn.market.Listing.infrastructure;

import com.daangn.market.Listing.application.dto.ListingResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ListingMyBatisRepository {

    Slice<ListingResponse> findAllListings(Long memberId, Integer regionId, Long lastListingId, int size, Pageable pageable);
}
