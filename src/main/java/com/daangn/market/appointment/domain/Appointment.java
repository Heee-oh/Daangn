package com.daangn.market.appointment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Table(name = "appointment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long id;

    @Column(name = "listing_id", nullable = false)
    private Long listingId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AppointmentStatus status;

    @Column(name = "notification_time")
    private Instant notificationTime;

    @Column(name = "meet_at", nullable = false)
    private Instant meetAt;

    @Column(name = "meet_place_text", length = 200)
    private String meetPlaceText;

    @Column(name = "meet_lat", precision = 10, scale = 7)
    private BigDecimal meetLat;

    @Column(name = "meet_lng", precision = 10, scale = 7)
    private BigDecimal meetLng;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
