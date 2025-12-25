package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.UserCommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserCommentLikeMapper extends BaseMapper<UserCommentLike> {

    @Select("select * from user_comment_likes where user_id = #{userId} and comment_id = #{commentId}")
    List<UserCommentLike> isExist(@Param("userId") Long userId, @Param("commentId") Long commentId);
}