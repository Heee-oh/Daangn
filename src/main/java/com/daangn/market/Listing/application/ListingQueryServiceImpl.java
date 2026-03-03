package com.daangn.market.Listing.application;

import com.daangn.market.Listing.application.dto.ListingDetailResponse;
import com.daangn.market.Listing.application.dto.ListingResponse;
import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.exception.ListingNotFoundException;
import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListingQueryServiceImpl implements ListingQueryService {

    private final ListingJpaRepository listingJpaRepository;

    /**
     * 삭제되지 않은 글 상세를 조회한다.
     */
    @Override
    public ListingDetailResponse getListing(Long listingId) {
        Listing listing = listingJpaRepository.findActiveByIdWithImages(listingId)
                .orElseThrow(ListingNotFoundException::new);

        return ListingDetailResponse.from(listing);
    }

    @Override
    public Slice<ListingResponse> getListings(Long memberId, Integer regionId, Long lastListingId) {
        PageRequest pageRequest = PageRequest.of(0, 20);

        return listingJpaRepository.findListings(memberId, regionId, lastListingId, 20, pageRequest);
    }
}