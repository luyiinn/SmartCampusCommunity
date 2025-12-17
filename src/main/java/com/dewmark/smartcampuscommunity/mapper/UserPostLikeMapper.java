package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.UserPostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserPostLikeMapper extends BaseMapper<UserPostLike> {

    @Select("select * from user_post_likes where user_id = #{userId} and post_id = #{postId}")
    List<UserPostLike> isExist(@Param("userId") Long userId, @Param("postId") Long postId);
}
