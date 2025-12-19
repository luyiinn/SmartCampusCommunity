package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostImageMapper extends BaseMapper<PostImage> {

    @Select("select * from post_image where post_id = #{postId}")
    List<PostImage> list(Long postId);
}
