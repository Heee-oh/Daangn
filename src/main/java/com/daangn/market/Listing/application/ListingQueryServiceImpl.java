package com.daangn.market.Listing.application;

import com.daangn.market.Listing.application.dto.ListingDetailResponse;
import com.daangn.market.Listing.application.dto.ListingResponse;
import com.daangn.market.Listing.domain.Listing;
import com.daangn.market.Listing.exception.ListingNotFoundException;
import com.daangn.market.Listing.infrastructure.ListingJpaRepository;
import com.daangn.market.chat.domain.ChatRoomStatus;
import com.daangn.market.chat.infrastructure.ChatRoomRepository;
import com.daangn.market.member.domain.Member;
import com.daangn.market.member.domain.exception.MemberNotFoundException;
import com.daangn.market.member.infrastructure.member.MemberJpaRepository;
import com.daangn.market.region.domain.Region;
import com.daangn.market.region.infrastructure.RegionJpaRepository;
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
    private final MemberJpaRepository memberJpaRepository;
    private final RegionJpaRepository regionJpaRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 삭제되지 않은 글 상세를 조회한다.
     */
    @Override
    public ListingDetailResponse getListing(Long listingId) {
        Listing listing = listingJpaRepository.findActiveByIdWithImages(listingId)
                .orElseThrow(ListingNotFoundException::new);

        Member seller = memberJpaRepository.findById(listing.getSellerId())
                .orElseThrow(MemberNotFoundException::new);
        String regionName = null;
        if (listing.getRegionId() != null) {
            regionName = regionJpaRepository.findById(listing.getRegionId())
                    .map(Region::getDongnm)
                    .orElse(null);
        }
        long chatCount = chatRoomRepository.countByListingIdAndStatus(listingId, ChatRoomStatus.ACTIVE);

        return ListingDetailResponse.from(listing, seller, regionName, chatCount);
    }

    @Override
    public Slice<ListingResponse> getListings(Long memberId, Integer regionId, Long lastListingId) {
        PageRequest pageRequest = PageRequest.of(0, 20);

        return listingJpaRepository.findListings(memberId, regionId, lastListingId, 20, pageRequest);
    }
}
