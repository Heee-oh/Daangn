package com.daangn.market.region.presentation.dto;

import com.daangn.market.region.domain.Region;

public record RegionSearchResponse(
        Integer regionId,
        String fullName,
        String dongnm
) {
    public static RegionSearchResponse from(Region region) {
        return new RegionSearchResponse(
                region.getId(),
                region.getAdmNm(),
                region.getDongnm()
        );
    }
}
