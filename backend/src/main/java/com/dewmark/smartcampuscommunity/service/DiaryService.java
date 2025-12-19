package com.dewmark.smartcampuscommunity.service;

import com.dewmark.smartcampuscommunity.pojo.dto.DiaryQueryDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.DiarySaveDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryListVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PageVO;

public interface DiaryService {
    DiaryDateVO getDate(Integer year);

    PageVO<DiaryListVO> getListByUser(DiaryQueryDTO diaryQueryDTO);

    Byte toggleDiaryLikeStatus(Long diaryId);

    void saveDiary(DiarySaveDTO diarySaveDTO);
}
