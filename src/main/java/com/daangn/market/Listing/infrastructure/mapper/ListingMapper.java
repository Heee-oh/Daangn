package com.daangn.market.Listing.infrastructure.mapper;

import com.daangn.market.Listing.application.dto.ListingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ListingMapper {

    List<ListingResponse> findNearbyListings(
            @Param("regionId") Integer regionId,
            @Param("lastListingId") Long lastListingId,
            @Param("size") int size
    );
}
