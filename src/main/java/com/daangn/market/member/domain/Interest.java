package com.daangn.market.member.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_interest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest {

    @Id
    @Column(name = "interest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Column(name = "listing_id", nullable = false, updatable = false)
    private Long listingId;

    @PrePersist
    void init() {
        if (id == null) {
            id = TsidCreator.getTsid().toLong();
        }
    }

    public Interest(Long listingId) {
        this.listingId = listingId;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
