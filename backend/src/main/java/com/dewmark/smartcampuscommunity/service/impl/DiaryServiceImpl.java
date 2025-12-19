package com.dewmark.smartcampuscommunity.service.impl;

import com.dewmark.smartcampuscommunity.context.BaseContext;
import com.dewmark.smartcampuscommunity.mapper.DiaryImageMapper;
import com.dewmark.smartcampuscommunity.mapper.DiaryMapper;
import com.dewmark.smartcampuscommunity.mapper.UserDiaryLikeMapper;
import com.dewmark.smartcampuscommunity.pojo.bo.DiaryQueryBO;
import org.springframework.transaction.annotation.Transactional;
import com.dewmark.smartcampuscommunity.pojo.dto.DiaryQueryDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.DiarySaveDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.Diary;
import com.dewmark.smartcampuscommunity.pojo.entity.DiaryImage;
import com.dewmark.smartcampuscommunity.pojo.entity.UserDiaryLike;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO;
import com.dewmark.smartcampuscommunity.pojo.vo.DiaryListVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PageVO;
import com.dewmark.smartcampuscommunity.service.DiaryService;
import com.dewmark.smartcampuscommunity.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final DiaryImageMapper diaryImageMapper;
    private final UserDiaryLikeMapper userDiaryLikeMapper;

    public DiaryServiceImpl(DiaryMapper diaryMapper,
            DiaryImageMapper diaryImageMapper,
            UserDiaryLikeMapper userDiaryLikeMapper) {
        this.diaryMapper = diaryMapper;
        this.diaryImageMapper = diaryImageMapper;
        this.userDiaryLikeMapper = userDiaryLikeMapper;
    }

    /**
     * 根据年份获取该用户日志记录情况
     * 
     * @param year
     * @return com.dewmark.smartcampuscommunity.pojo.vo.DiaryDateVO
     * @author dewMark
     * @create 1/12/2025
     **/
    @Override
    public DiaryDateVO getDate(Integer year) {
        DiaryDateVO vo = new DiaryDateVO();
        Long userId = BaseContext.getCurrentId();
        List<LocalDateTime> dates = diaryMapper.getDates(userId, year);
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

    /**
     * 日志分页查询
     * 
     * @param diaryQueryDTO
     * @return com.dewmark.smartcampuscommunity.pojo.vo.PageVO<com.dewmark.smartcampuscommunity.pojo.vo.DiaryListVO>
     * @author dewMark
     * @create 8/12/2025
     **/
    @Override
    public PageVO<DiaryListVO> getListByUser(DiaryQueryDTO diaryQueryDTO) {

        // 构建查找BO
        // 起始索引计算
        Integer offset = (diaryQueryDTO.getPage() - 1) * diaryQueryDTO.getSize();
        // 构建
        DiaryQueryBO diaryQueryBO = new DiaryQueryBO()
                .builder()
                .offset(offset)
                .keyword(diaryQueryDTO.getKeyword())
                .userId(diaryQueryDTO.getUserId())
                .limit(diaryQueryDTO.getSize())
                .build();

        // 若为当前用户则查询所有列表数据，如不是则仅展示公开数据
        Long currentId = BaseContext.getCurrentId();
        Long userId = diaryQueryDTO.getUserId();
        Integer isOneSelf = userId == currentId ? 1 : 0;
        diaryQueryBO.setIsOneSelf(isOneSelf);

        // 获取分页数据
        List<Diary> diaries = diaryMapper.list(diaryQueryBO);
        // 获取总数
        Long counts = diaryMapper.count(diaryQueryBO);
        // 处理数据为响应所需数据结构
        List<DiaryListVO> diaryListVOS = new ArrayList<>();
        for (Diary diary : diaries) {
            DiaryListVO diaryListVO = new DiaryListVO();
            BeanUtils.copyProperties(diary, diaryListVO);
            diaryListVO.setContentSummary(diary.getContent());
            diaryListVO.setImages(diaryImageMapper.getImagesByDiaryId(diary.getId()));
            // 判断用户是否点赞
            // 查询是否存在点赞记录
            List<UserDiaryLike> userDiaryLikes = userDiaryLikeMapper.isExist(currentId, diary.getId());
            if (userDiaryLikes != null && !userDiaryLikes.isEmpty()) { // 存在记录，存储其状态
                diaryListVO.setIsLike(userDiaryLikes.get(0).getLikeStatus());
            } else { // 不存在记录是为未点赞
                diaryListVO.setIsLike((byte) 0);
            }
            // 存入构建好的diaryListVOS
            diaryListVOS.add(diaryListVO);
        }

        PageVO<DiaryListVO> pageVO = new PageVO();
        pageVO.setTotal(counts);
        pageVO.setList(diaryListVOS);

        return pageVO;
    }

    @Override
    @Transactional
    public Byte toggleDiaryLikeStatus(Long diaryId) {
        Long userId = BaseContext.getCurrentId();

        // 查询当前点赞状态
        List<UserDiaryLike> existingLikes = userDiaryLikeMapper.isExist(userId, diaryId);
        Byte currentStatus = existingLikes != null && !existingLikes.isEmpty() ? existingLikes.get(0).getLikeStatus()
                : 0;

        // 计算新的点赞状态和点赞数变化
        Byte newStatus = currentStatus == 1 ? (byte) 0 : 1;
        Integer likeCountChange = newStatus == 1 ? 1 : -1;

        // 更新点赞状态和日记点赞数
        userDiaryLikeMapper.saveOrUpdateLikeStatus(userId, diaryId, newStatus);
        userDiaryLikeMapper.updateDiaryLikeCount(diaryId, likeCountChange);

        return newStatus;
    }

    @Override
    @Transactional
    public void saveDiary(DiarySaveDTO diarySaveDTO) {
        Long userId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        // 创建Diary实体
        Diary diary = new Diary();
        diary.setTitle(diarySaveDTO.getTitle());
        diary.setContent(diarySaveDTO.getContent());
        diary.setIsPublic(diarySaveDTO.getIsPublic());
        diary.setStatus(diarySaveDTO.getStatus());
        diary.setUserId(userId);
        diary.setViewCount(0);
        diary.setLikeCount(0);
        diary.setIsDeleted(false);
        diary.setCreatedAt(now);
        diary.setUpdatedAt(now);

        // 保存日志基本信息
        diaryMapper.insert(diary);

        // 保存日志图片
        List<String> images = diarySaveDTO.getImages();
        if (images != null && !images.isEmpty()) {
            List<DiaryImage> diaryImages = new ArrayList<>();
            for (String imagePath : images) {
                DiaryImage diaryImage = new DiaryImage();
                diaryImage.setDiaryId(diary.getId());
                diaryImage.setImagePath(imagePath);
                diaryImage.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                diaryImages.add(diaryImage);
            }
            diaryImageMapper.saveBatch(diaryImages);
        }
    }
}
