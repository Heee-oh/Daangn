package com.daangn.market.common.domain.id;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class MemberId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "member_id")
    private Long value;

    public static MemberId generate() {
        return new MemberId(TsidCreator.getTsid().toLong());
    }

    public static MemberId of(Long value) {
        return new MemberId(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }


}
