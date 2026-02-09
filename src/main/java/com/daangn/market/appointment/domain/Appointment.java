package com.daangn.market.appointment.domain;

import com.daangn.market.Listing.domain.HopeLocation;
import com.daangn.market.common.domain.id.AppointmentId;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;

import java.time.Instant;

public class Appointment {
    private AppointmentId id;
    private ListingId listingId;
    private MemberId sellerId;
    private MemberId buyerId;
    private AppointmentStatus status; // SCHEDULED/CANCELED/DONE
    private Instant notificationTime; // nullable
    private Instant meetAt;
    private HopeLocation meetLocation; // text + lat/lng
    private Instant createdAt;
    private Instant updatedAt;
}
