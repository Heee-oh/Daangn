package com.daangn.market.appointment.application.dto;

import java.time.Instant;

public record AppointmentResponse(
        Long appointmentId,
        Instant meetAt,
        Integer reminderMinutes
) {
}
