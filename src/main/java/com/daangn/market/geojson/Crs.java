package com.daangn.market.geojson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Crs {
    private String type;
    private Properties properties;
}
