package com.daangn.market.service;

import com.daangn.market.domain.Location;
import com.daangn.market.domain.Post;
import com.daangn.market.domain.TradeStatus;
import com.daangn.market.dto.request.PostReqDto;
import com.daangn.market.dto.response.PostDetailDto;
import com.daangn.market.dto.response.PostImageDto;
import com.daangn.market.mapper.PostMapper;
import com.daangn.market.repository.member.MemberRepository;
import com.daangn.market.repository.post.PostImageRepository;
import com.daangn.market.repository.post.PostRepository;
import com.daangn.market.repository.region_dong.RegionDongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    private final PostImageRepository postImageRepository;
    private final RegionDongRepository regionDongRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public Post savePost(PostReqDto postReqDto) {
        Post post = Post.builder()
                .regionId(postReqDto.regionId())
                .memberId(postReqDto.memberId())
                .category(postReqDto.category())
                .title(postReqDto.title())
                .body(postReqDto.body())
                .price(postReqDto.price())
                .location(postReqDto.location())
                .state(TradeStatus.ON_SALE)
                .isFree(postReqDto.isFree())
                .build();


        return postRepository.save(post);
    }

    // 판매글 조회 (postId 값을 이용한 단일 게시글)
    @Transactional(readOnly = true)
    public PostDetailDto findPostDetail(long memberId, long postId, int regionId, Location location) {
        if (!memberRepository.existsMemberById(memberId)) {
            throw new NoSuchElementException("멤버가 존재하지 않음");
        }

        if (!regionDongRepository.existsRegionDongById(regionId)) {
            throw new NoSuchElementException("동네가 존재하지 않음");
        }


        // 게시글 디테일 DTO 조회
        PostDetailDto postDetailDto = postMapper.findPostDetailById(postId, new Location(location.getLongitude(), location.getLatitude()))
                .orElseThrow(() -> new NoSuchElementException("판매글이 존재하지 않음 postId : " + postId));

        // 이미지들 조회
        List<PostImageDto> images = postImageRepository.findAllPostImagesByPostIdOrderBySortOrder(postId);

        // DTO에 이미지 초기화
        postDetailDto.initPostImages(images);
        return postDetailDto;
    }



    // 무한 스크롤 조회

    // 판매글 삭제

    // 판매글 상태 업데이트

    // 판매글 가격 업데이트

    // 판매글 제목 업데이트

    // 판매글 내용 업데이트

    // 판매글 대량 업데이트
    @Transactional
    public void updatePost(long id) { // 파라미터에 DTO로 받아오기
        Post post = postRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없음"));

        // 더티 체킹으로 업데이트
    }

    // 판매글 이미지 업데이트

    // 조회수, 좋아요 업데이트는 redis로 모아뒀다 일정 시간 간격으로 업데이트

    // 숨기기  <-- 사요자가 대량의 게시글을 숨겼다면 어떻게 처리할 것인가? (너무 많으면 성능 저하)

}
