package com.daangn.market.member.domain;

import com.daangn.market.common.domain.id.InterestId;
import com.daangn.market.common.domain.id.ListingId;
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

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Embedded
    private ListingId listingId;

    private Instant createdAt;

    @PrePersist
    void init() {
        if (id == null) {
            id = new InterestId(TsidCreator.getTsid().toLong());
        }
    }

    public Interest(ListingId listingId) {
        this.listingId = listingId;
        createdAt = Instant.now();
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
