package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select * from comment where post_id = #{postId} order By create_at desc")
    List<Comment> list(Long postId);

    @Update("update comment set like_count = like_count + #{count} where id = #{commentId}")
    int updateLikeCount(@Param("commentId") Long commentId, @Param("count") Integer count);

}
