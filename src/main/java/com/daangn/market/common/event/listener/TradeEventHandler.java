package com.daangn.market.common.event.listener;

import com.daangn.market.common.event.events.ListingSoldOutEvent;
import com.daangn.market.trade.domain.Trade;
import com.daangn.market.trade.infrastructure.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeEventHandler {

    private final TradeRepository tradeRepository;

    /**
     * 판매 완료 이벤트를 구독하여 거래 완료 데이터를 생성한다.
     */
    @EventListener
    public void handle(ListingSoldOutEvent event) {
        if (tradeRepository.existsByListingId(event.listingId())) {
            return;
        }

        tradeRepository.save(Trade.complete(
                event.listingId(),
                event.sellerId(),
                event.buyerId(),
                event.price()
        ));
    }
}
