package com.daangn.market.service;

import com.daangn.market.domain.Location;
import com.daangn.market.domain.Member;
import com.daangn.market.domain.Post;
import com.daangn.market.dto.request.MemberSignDto;
import com.daangn.market.dto.request.PostReqDto;
import com.daangn.market.dto.response.PostDetailDto;
import com.daangn.market.repository.region_dong.RegionDongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    MemberService memberService;

    static long id;
    static Post[] arr;

    static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void init() {
        Member member = memberService.saveMember(new MemberSignDto("abc", "aaa", "010-2222-2222"));
        id = member.getId();
        arr = new Post[5];

        Point point = geometryFactory.createPoint(new Coordinate(126.9780d, 37.5665d));

        PostReqDto dto1 = new PostReqDto(
                20,
                id,
                3,
                "서울 시청 근처 전기포트 팝니다",
                "이사 준비로 내놓습니다. 상태 매우 좋아요.",
                15000,
                point,
                false
        );


         arr[0] = postService.savePost(dto1);

    }


    // ST_X(geom) → 경도(lon)
    // ST_Y(geom) → 위도(lat)
    @Test
    @Transactional
    @DisplayName("판매글 조회")
    void findPostDetailTest() {
        // given
        Member member = memberService.findMember(id);
        Location location = new Location(127.978d, 37.5662d);
        PostDetailDto postDetail = postService.findPostDetail(member.getId(), arr[0].getId(), arr[0].getRegionId(), location);

        // when
        int distance = (int)haversine(
                126.978d, 37.5662d,
                location.getLongitude(), location.getLatitude()
        );

        // then
        Assertions.assertThat(member).isNotNull();
        Assertions.assertThat(distance).isEqualTo(postDetail.getDistance());
    }

    public static double haversine(double lon1, double lat1, double lon2, double lat2) {
        double R = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}