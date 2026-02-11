package com.daangn.market.member.domain;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor
public class PhoneNumber {
    private static final Pattern PATTERN = Pattern.compile("^\\d{10,11}$");
    private String value;


    public PhoneNumber(String value) {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("전화번호 형식이 맞지 않음");
        }

        this.value = value;
    }

}
