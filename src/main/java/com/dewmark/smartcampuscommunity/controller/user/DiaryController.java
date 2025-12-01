package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.DataNotIlegalException;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 日志板块控制器类
 * @author: dewMark
 * @date: 2025/11/30
 **/
@RestController
@RequestMapping("/api/diary")
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;
    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /**
     *  查询用户日志记录日期情况
     * @param year
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO>
     * @author dewMark
     * @create 1/12/2025
     **/
    @GetMapping("/dates")
    public Result<DiaryDateVO> getDate(@RequestParam Integer year){
        log.info("查询用户日志记录日期情况，年份：{}",year);

        if (year == null || year.equals("")){
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }

        DiaryDateVO diaryDateVO = diaryService.getDate(year);

        return Result.success(diaryDateVO);
    }

}
