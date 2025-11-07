package com.daangn.market.service;

import com.daangn.market.domain.Post;
import com.daangn.market.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post savePost() {
        Post post = new Post();

        return postRepository.save(post);
    }

    // 판매글 조회 (id 값을 이용한 단일 게시글)


    // 무한 스크롤 조회

    // 판매글 삭제

    // 판매글 상태 업데이트

    // 판매글 가격 업데이트

    // 판매글 제목 업데이트

    // 판매글 내용 업데이트

    // 판매글 대량 업데이트
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
