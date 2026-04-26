package com.daangn.market.trade.infrastructure;

import com.daangn.market.trade.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    boolean existsByListingId(Long listingId);
}
