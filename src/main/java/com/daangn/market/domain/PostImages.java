package com.daangn.market.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PostImages {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long postId;

    private String imgSrc;

    private int sortOrder;

}
