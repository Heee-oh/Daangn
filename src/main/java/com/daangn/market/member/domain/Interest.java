package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.InterestId;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest {
    @EmbeddedId
    private InterestId id;
    @Embedded
    private MemberId memberId;
    @Embedded
    private ListingId listingId;

    private Instant createdAt;

    @PrePersist
    void init() {
        if (id == null) {
            id = new InterestId(TsidCreator.getTsid().toLong());
        }
    }

    public Interest(ListingId listingId, MemberId memberId) {
        this.listingId = listingId;
        this.memberId = memberId;
        createdAt = Instant.now();
    }
}
