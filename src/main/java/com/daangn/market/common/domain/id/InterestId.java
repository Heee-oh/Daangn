package com.daangn.market.common.domain.id;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class InterestId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "interest_id")
    private Long value;
}
