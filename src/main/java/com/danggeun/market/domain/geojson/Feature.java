package com.danggeun.market.domain.geojson;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Feature {
    private String type;
    private Geometry geometry;
    private FeatureProperties properties;
}
