package com.daangn.market.notification.infrastructure;

import com.daangn.market.notification.domain.KeywordSubscription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordSubscriptionRepository extends JpaRepository<KeywordSubscription, Long> {

    List<KeywordSubscription> findByRegionIdOrRegionIdIsNull(Integer regionId);

    void deleteByMemberId(Long memberId);
}
