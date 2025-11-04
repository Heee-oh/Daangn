package com.danggeun.market.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MemberSelectRegion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long member_id;

    private int region_dong_id;
    private int boundary;
}
