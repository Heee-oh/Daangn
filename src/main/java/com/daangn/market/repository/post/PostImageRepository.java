package com.daangn.market.repository.post;

import com.daangn.market.domain.PostImages;
import com.daangn.market.dto.response.PostImageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImages, Long> {

    @Query("select pi.id, pi.imgSrc, pi.sortOrder from PostImages pi where pi.postId = :postId order by pi.sortOrder asc")
    List<PostImageDto> findAllPostImagesByPostIdOrderBySortOrder(@Param("postId") long postId);
}
