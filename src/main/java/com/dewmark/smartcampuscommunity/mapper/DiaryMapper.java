package com.dewmark.smartcampuscommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dewmark.smartcampuscommunity.pojo.bo.DiaryQueryBO;
import com.dewmark.smartcampuscommunity.pojo.entity.Diary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {


    List<LocalDateTime> getDates(@Param("userId") Long userId,@Param("year") Integer year);

    List<Diary> list(DiaryQueryBO diaryQueryBO);

    Long count(DiaryQueryBO queryBO);
}
