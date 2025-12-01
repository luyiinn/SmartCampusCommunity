package com.dewmark.smartcampuscommunity.service.impl;

import com.dewmark.smartcampuscommunity.context.BaseContext;
import com.dewmark.smartcampuscommunity.mapper.DiaryMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO;
import com.dewmark.smartcampuscommunity.service.DiaryService;
import com.dewmark.smartcampuscommunity.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 日志板块实现类
 * @author: dewMark
 * @date: 2025/11/30
 **/
@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;
    @Autowired
    public DiaryServiceImpl(DiaryMapper diaryMapper) {
        this.diaryMapper = diaryMapper;
    }

    /**
     * 根据年份获取该用户日志记录情况
     * @param year
     * @return com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO
     * @author dewMark
     * @create 1/12/2025
     **/
    @Override
    public DiaryDateVO getDate(Integer year) {
        DiaryDateVO vo = new DiaryDateVO();
        Long userId = BaseContext.getCurrentId();
        List<LocalDateTime> dates = diaryMapper.getDates(userId,year);
        // 格式化为 yyyy-MM-dd 字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> formattedDates = dates.stream()
                .map(dateTime -> dateTime.format(formatter))
                .distinct() // 去重
                .collect(Collectors.toList());

        // 设置到 VO 对象中
        vo.setDates(formattedDates);
        return vo;
    }
}
