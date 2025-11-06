package com.danggeun.market.domain;

public enum MemberStatus {
    ACTIVE("활성"), // 활성
    SUSPENDED("정지"), // 정지
    DEACTIVATED("비활성화 (7일 후 삭제)");

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
