package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.DataNotIlegalException;
import com.dewmark.smartcampuscommunity.pojo.dto.DiaryQueryDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.DiarySaveDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryListVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PageVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 日记板块控制器类
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
     * 查询用户日记记录日期情况
     * 
     * @param year
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO>
     * @author dewMark
     * @create 1/12/2025
     **/
    @GetMapping("/dates")
    public Result<DiaryDateVO> getDate(@RequestParam Integer year) {
        log.info("查询用户日记记录日期情况，年份：{}", year);

        if (year == null || year.equals("")) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }

        DiaryDateVO diaryDateVO = diaryService.getDate(year);

        return Result.success(diaryDateVO);
    }

    /**
     * 获取日记分页列表
     * 
     * @param diaryQueryDTO
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.PageVO<com.dewmark.smartcampuscommunity.pojo.vo.DiaryListVO>>
     * @author dewMark
     * @create 8/12/2025
     **/
    @GetMapping("/list")
    public Result<PageVO<DiaryListVO>> getDiaryList(DiaryQueryDTO diaryQueryDTO) {
        log.info("查看用户日记列表，{}", diaryQueryDTO);

        if (diaryQueryDTO == null || diaryQueryDTO.equals("")) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }

        PageVO<DiaryListVO> list = diaryService.getListByUser(diaryQueryDTO);

        return Result.success(list);
    }

    /**
     * 切换日记点赞状态
     * 
     * @param diaryId 日记ID
     * @return com.dewmark.smartcampuscommunity.result.Result<java.lang.Byte>
     * @author dewMark
     * @create 19/12/2025
     **/
    @PostMapping("/like/{diaryId}")
    public Result<Byte> toggleDiaryLikeStatus(@PathVariable Long diaryId) {
        log.info("切换日记点赞状态，日记ID：{}", diaryId);

        if (diaryId == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }

        Byte newStatus = diaryService.toggleDiaryLikeStatus(diaryId);
        return Result.success(newStatus);
    }

    /**
     * 新增日记
     * 
     * @param diarySaveDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 19/12/2025
     **/
    @PostMapping("/add")
    public Result saveDiary(@RequestBody DiarySaveDTO diarySaveDTO) {
        if (diarySaveDTO == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        log.info("发布日记: {}", diarySaveDTO);
        diaryService.saveDiary(diarySaveDTO);
        return Result.success();
    }

}
