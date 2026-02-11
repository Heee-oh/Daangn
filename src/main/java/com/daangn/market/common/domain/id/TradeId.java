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
public class TradeId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "trade_id")
    private Long value;

    public static TradeId generate() {
        return new TradeId(TsidCreator.getTsid().toLong());
    }
}