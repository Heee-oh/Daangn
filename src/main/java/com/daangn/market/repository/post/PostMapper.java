package com.daangn.market.repository.post;

import com.daangn.market.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    Post findById(Long id);

}
