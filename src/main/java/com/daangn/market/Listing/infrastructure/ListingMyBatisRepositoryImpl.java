package com.daangn.market.Listing.infrastructure;

import com.daangn.market.Listing.application.dto.ListingResponse;
import com.daangn.market.Listing.infrastructure.mapper.ListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ListingMyBatisRepositoryImpl implements ListingMyBatisRepository {

    private final ListingMapper listingMapper;

    @Transactional(readOnly = true)
    public Slice<ListingResponse> findAllListings(Long memberId, Integer regionId, Long lastListingId, int size, Pageable pageable) {

        int pageSize = pageable.getPageSize();

        List<ListingResponse> nearbyListings = listingMapper.findNearbyListings(regionId, lastListingId, size);

        boolean hasNext = nearbyListings.size() > pageSize;

        if (hasNext) {
            nearbyListings.removeLast();
        }

        return new SliceImpl<>(nearbyListings, pageable, hasNext);



    }
}
