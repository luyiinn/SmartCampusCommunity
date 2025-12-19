package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    List<String> selectTagNamesByPostId(Long id);

    // 使用次数加 1
    @Update("UPDATE tag set use_count = use_count + 1 where id = #{id}")
    Integer useCountUp(Long id);
}
