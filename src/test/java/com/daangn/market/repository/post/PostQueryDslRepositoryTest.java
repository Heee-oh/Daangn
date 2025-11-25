package com.daangn.market.repository.post;

import com.daangn.market.domain.Location;
import com.daangn.market.domain.Member;
import com.daangn.market.domain.Post;
import com.daangn.market.dto.request.MemberSignDto;
import com.daangn.market.dto.request.PostReqDto;
import com.daangn.market.dto.response.PostDetailDto;
import com.daangn.market.service.MemberService;
import com.daangn.market.service.PostService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PostQueryDslRepositoryTest {

    @Autowired
    PostQueryDslRepository queryDslRepository;

    @Autowired
    PostService service;

    @Autowired
    MemberService memberService;


    static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    Post[] arr;

    @BeforeEach
    void init() {
        arr = new Post[5];
        Member member = memberService.saveMember(new MemberSignDto("abc", "aaa", "010-2222-2222"));
        Point point = geometryFactory.createPoint(new Coordinate(126.9780d, 37.5665d));
        PostReqDto dto1 = new PostReqDto(
                20,
                member.getId(),
                3,
                "서울 시청 근처 전기포트 팝니다",
                "이사 준비로 내놓습니다. 상태 매우 좋아요.",
                15000,
                point,
                false
        );


        arr[0] = service.savePost(dto1);

    }
    @Test
    @DisplayName("querydsl을 사용한 post 검색 성공")
    @Transactional
    void findPostDetailByIdSuccess() {
        Optional<PostDetailDto> postDetailById
                = queryDslRepository.findPostDetailById(arr[0].getId(), new Location(126.9782d, 37.5662d));

        //  null 여부 확인
        Assertions.assertThat(postDetailById).isNotEmpty();

        // id 비교
        PostDetailDto postDetailDto = postDetailById.get();
        Assertions.assertThat(postDetailDto.getId()).isEqualTo(arr[0].getId());

        // 거리 비교
        double expected = haversine(126.9780d, 37.5665d, 126.9782d, 37.5662d);
        Assertions.assertThat(postDetailDto.getDistance())
                .isCloseTo(expected, Assertions.within(0.001));

        log.info("distance = {}", postDetailDto.getDistance());


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