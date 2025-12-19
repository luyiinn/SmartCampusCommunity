package com.dewmark.smartcampuscommunity.mapper;

import com.dewmark.smartcampuscommunity.pojo.entity.UserDiaryLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDiarytLikeMapper {

    @Select("select * from user_diary_likes where user_id = #{currentId} and diary_id = #{diaryId}")
    List<UserDiaryLike> isExist(@Param("currentId") Long currentId,@Param("diaryId") Long diaryId);
}
