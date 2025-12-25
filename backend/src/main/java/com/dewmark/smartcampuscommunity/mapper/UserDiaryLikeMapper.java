package com.dewmark.smartcampuscommunity.mapper;

import com.dewmark.smartcampuscommunity.pojo.entity.UserDiaryLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserDiaryLikeMapper {
    @Select("select * from user_diary_likes where user_id = #{currentId} and diary_id = #{diaryId}")
    List<UserDiaryLike> isExist(@Param("currentId") Long currentId, @Param("diaryId") Long diaryId);

    @Update("INSERT INTO user_diary_likes (user_id, diary_id, like_status) VALUES (#{userId}, #{diaryId}, #{likeStatus}) ON DUPLICATE KEY UPDATE like_status = #{likeStatus}")
    void saveOrUpdateLikeStatus(@Param("userId") Long userId, @Param("diaryId") Long diaryId,
            @Param("likeStatus") Byte likeStatus);

    @Update("UPDATE diary SET like_count = like_count + #{change} WHERE id = #{diaryId}")
    void updateDiaryLikeCount(@Param("diaryId") Long diaryId, @Param("change") Integer change);
}
