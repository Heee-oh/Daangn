package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.id.RegionId;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HopeLocation {
    private RegionId regionId;
    private double lat;
    private double log;


    public HopeLocation(RegionId regionId, double lat, double log) {
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
