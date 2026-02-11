package com.daangn.market.common.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class RegionId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "region_id")
    private Integer value;
}
