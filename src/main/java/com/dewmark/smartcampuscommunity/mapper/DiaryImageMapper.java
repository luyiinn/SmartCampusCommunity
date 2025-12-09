package com.dewmark.smartcampuscommunity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiaryImageMapper {

    @Select("select image_path from diary_images where diary_id = #{id}")
    List<String> getImagesByDiaryId(Long id);
}
