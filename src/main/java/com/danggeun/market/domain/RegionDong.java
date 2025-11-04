package com.danggeun.market.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;


@Entity
@Immutable
@Table(name = "region_dong")
public class RegionDong {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 전체 행정 명
    @Column(name = "adm_nm")
    private String admName;
    // 행정 코드
    @Column(name = "adm_cd")
    private String admCode;
    // 법정동 코드
    @Column(name = "adm_cd2")
    private String admCode2;
    // 시군구 코드
    @Column(name = "sgg")
    private String sggCode;
    // 시군구 이름
    @Column(name = "sggnm")
    private String sggName;
    // 시도 코드
    @Column(name = "sido")
    private String sidoCode;
    // 시도 네임
    @Column(name = "sidonm")
    private String sidoname;
    // 동 이름
    @Column(name = "dongnm")
    private String dongName;

    // wkt 형태로 저장되어있음
    private String geom;


}
