package com.daangn.market.common.domain.id;

import com.github.f4b6a3.tsid.TsidCreator;
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
public class SubscriptionId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "subscription_id")
    private Long value;

    public static SubscriptionId generate() {
        return new SubscriptionId(TsidCreator.getTsid().toLong());
    }
}