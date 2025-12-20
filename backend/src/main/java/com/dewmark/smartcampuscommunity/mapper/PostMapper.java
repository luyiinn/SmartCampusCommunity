package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.bo.PostQueryBO;
import com.dewmark.smartcampuscommunity.pojo.entity.Post;
import com.dewmark.smartcampuscommunity.pojo.vo.PostListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {


    Long selectPostCount(PostQueryBO queryBO);

    List<PostListVO> selectPostListWithSummary(PostQueryBO queryBO);

    Long selectLikedPostCount(PostQueryBO queryBO);

    List<PostListVO> selectLikedPostListWithSummary(PostQueryBO queryBO);

    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE id = #{id}")
    Integer commentCountUp(Long id);
}
