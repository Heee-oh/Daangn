package com.daangn.market.mapper;

import com.daangn.market.domain.Location;
import com.daangn.market.dto.request.PostReqDto;
import com.daangn.market.dto.response.PostDetailDto;
import com.daangn.market.dto.response.PostImageDto;
import com.daangn.market.repository.post.PostRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    Optional<PostDetailDto> findPostDetailById(@Param("postId") long id, @Param("location") Location location);


    List<PostImageDto> findPostImagesByPostId(@Param("postId") long postId);
}
