package com.dewmark.smartcampuscommunity.mapper;

import com.dewmark.smartcampuscommunity.pojo.entity.DiaryImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface DiaryImageMapper {

    @Select("select image_path from diary_images where diary_id = #{id}")
    List<String> getImagesByDiaryId(Long id);

    @Insert("insert into diary_images(diary_id, image_path, created_at) values(#{diaryId}, #{imagePath}, #{createdAt})")
    void insert(DiaryImage diaryImage);

    void saveBatch(List<DiaryImage> diaryImages);
}
