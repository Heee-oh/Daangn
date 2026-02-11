package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.InterestId;
import com.daangn.market.common.domain.id.ListingId;
import com.daangn.market.common.domain.id.MemberId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class Interest {
    @EmbeddedId
    private InterestId id;
    @Embedded
    private MemberId memberId;
    @Embedded
    private ListingId listingId;
    private Instant createAt;
}
