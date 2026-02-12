package com.daangn.market.Listing.domain;

import com.daangn.market.common.domain.id.RegionId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HopeLocation {

    @Embedded
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

        this.regionId = regionId;
        this.lat = lat;
        this.log = log;
    }

}
