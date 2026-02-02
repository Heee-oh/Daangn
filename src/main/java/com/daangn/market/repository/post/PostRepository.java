package com.daangn.market.repository.post;

import com.daangn.market.domain.Post;
import com.daangn.market.domain.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PostRepository extends JpaRepository<Post, Long> {


    // 거래 상태 변경
    @Modifying
    @Query("UPDATE Post p SET p.state =:status WHERE p.id =:id")
    int updatePostStatusById(@Param("status") TradeStatus status, @Param("id") long id);

    @Modifying
    @Query("UPDATE Post p SET p.title =:title WHERE p.id=:id")
    int updatePostTitleById(@Param("title")String title, @Param("id") long id);

    @Modifying
    @Query("UPDATE Post p SET p.body =:body WHERE p.id=:id")
    int updatePostBodyById(@Param("body")String body, @Param("id") long id);

    @Modifying
    @Query("UPDATE Post p SET p.title =:title WHERE p.id=:id")
    int updatePriceById(@Param("title")String title, @Param("id") long id);



}
