package com.daangn.market.Listing.application;

import com.daangn.market.Listing.application.dto.ListingDetailResponse;
import com.daangn.market.Listing.application.dto.ListingResponse;
import com.daangn.market.member.application.dto.InterestResponse;
import org.springframework.data.domain.Slice;

public interface ListingQueryService {
    ListingDetailResponse getListing(Long listingId);
    Slice<ListingResponse> getListings(Long memberId, Integer regionId, Long lastListingId);
}

