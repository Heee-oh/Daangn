package com.daangn.market.region.domain;

import com.daangn.market.common.domain.id.RegionId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Region {

    @EmbeddedId
    private RegionId id;

    @Column(name = "adm_nm", length = 50, nullable = false)
    private String admNm;

    @Column(name = "adm_cd", length = 8, nullable = false)
    private String admCd;

    @Column(name = "adm_cd2", length = 10, nullable = false)
    private String admCd2;

    @Column(length = 5, nullable = false)
    private String sgg;

    @Column(length = 2, nullable = false)
    private String sido;

    @Column(length = 20, nullable = false)
    private String sidonm;

    @Column(length = 20, nullable = false)
    private String sggnm;

    @Column(length = 20, nullable = false)
    private String dongnm;

    /**
     * SRID 4326 (WGS84) MultiPolygon 데이터
     * hibernate-spatial 의존성이 필요합니다.
     */
    @Column(columnDefinition = "geometry(MultiPolygon, 4326)", nullable = false)
    private MultiPolygon geom;

}