package com.daangn.market.common.domain.id;

import jakarta.persistence.Column;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


@NoArgsConstructor
public class AppointmentId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "appointment_id")
    private Long value;
}
