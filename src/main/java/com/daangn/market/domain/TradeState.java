package com.daangn.market.domain;

public enum TradeState {
    ON_SALE("판매중"),
    RESERVED("예약 중"),
    SOLD_OUT("판매 완료");

    private final String description;

    TradeState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
