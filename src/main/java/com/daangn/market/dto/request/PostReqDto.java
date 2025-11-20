package com.daangn.market.dto.request;

import org.locationtech.jts.geom.Point;

public record PostReqDto(int regionId,
                         long memberId,
                         int category,
                         String title,
                         String body,
                         int price,
                         Point location,
                         boolean isFree
                         ) {

}
