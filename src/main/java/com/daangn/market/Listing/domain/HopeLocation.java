package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.id.RegionId;

public record HopeLocation(RegionId regionId, double lat, double log) {

    public HopeLocation {
        if (regionId == null) {
            throw new IllegalArgumentException("regionId is required");
        }
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException("latitude out of range");
        }
        if (log < -180.0 || log > 180.0) {
            throw new IllegalArgumentException("longitude out of range");
        }
    }

}
